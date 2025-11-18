package com.lokoko.global.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.brand.domain.repository.BrandRepository;
import com.lokoko.domain.brand.exception.BrandNotFoundException;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.domain.repository.CreatorRepository;
import com.lokoko.domain.creator.exception.CreatorNotFoundException;
import com.lokoko.domain.customer.domain.entity.Customer;
import com.lokoko.domain.customer.domain.repository.CustomerRepository;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import com.lokoko.global.auth.exception.ErrorMessage;
import com.lokoko.global.auth.exception.InvalidRoleException;
import com.lokoko.global.auth.exception.OauthException;
import com.lokoko.global.auth.exception.RoleChangeNotAllowedException;
import com.lokoko.global.auth.exception.StateValidationException;
import com.lokoko.global.auth.exception.UserNotCompletedSignUpException;
import com.lokoko.global.auth.jwt.dto.LoginResponse;
import com.lokoko.global.auth.jwt.exception.TokenInvalidException;
import com.lokoko.global.auth.jwt.utils.CookieUtil;
import com.lokoko.global.auth.jwt.utils.JwtExtractor;
import com.lokoko.global.auth.jwt.utils.JwtProvider;
import com.lokoko.global.auth.provider.google.config.GoogleOAuthClient;
import com.lokoko.global.auth.provider.google.config.GoogleProperties;
import com.lokoko.global.auth.provider.google.dto.GoogleProfileDto;
import com.lokoko.global.auth.provider.google.dto.GoogleTokenDto;
import com.lokoko.global.auth.provider.google.dto.RoleUpdateResponse;
import com.lokoko.global.auth.provider.google.dto.response.AfterLoginUserNameResponse;
import com.lokoko.global.auth.provider.line.config.LineOAuthClient;
import com.lokoko.global.auth.provider.line.config.LineProperties;
import com.lokoko.global.auth.provider.line.dto.LineProfileDto;
import com.lokoko.global.auth.provider.line.dto.LineTokenDto;
import com.lokoko.global.auth.provider.line.dto.LineUserInfoDto;
import com.lokoko.global.utils.GoogleConstants;
import com.lokoko.global.utils.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static com.lokoko.global.auth.jwt.utils.JwtProvider.EMAIL_CLAIM;
import static com.lokoko.global.utils.LineConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final StateService stateService;
    private final LineOAuthClient oAuthClient;
    private final GoogleOAuthClient googleOAuthClient;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final CreatorRepository creatorRepository;
    private final BrandRepository brandRepository;
    private final JwtProvider jwtProvider;
    private final JwtExtractor jwtExtractor;
    private final LineProperties props;
    private final GoogleProperties googleProps;
    private final RedisUtil redisUtil;
    private final CookieUtil cookieUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_KEY_PREFIX = "refreshToken:";

    @Value("${lokoko.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    @Transactional
    public LoginResponse loginWithLine(String code, String state) {
        try {
            LineTokenDto tokenResp = oAuthClient.issueToken(code);
            DecodedJWT idToken = JWT.decode(tokenResp.id_token());
            String email = idToken.getClaim(EMAIL_CLAIM).asString();

            LineProfileDto profile = oAuthClient.fetchProfile(tokenResp.access_token());
            String lineUserId = profile.userId();

            LineUserInfoDto userInfo = oAuthClient.fetchUserInfo(tokenResp.access_token());
            String displayName = userInfo.name();

            Optional<User> userOpt = userRepository.findByLineId(lineUserId);
            User user;
            OauthLoginStatus loginStatus;

            if (userOpt.isPresent()) {
                user = userOpt.get();
                user.updateLastLoginAt();
                user.updateEmail(email);
                user.updateDisplayName(displayName);
                userRepository.save(user);
                loginStatus = OauthLoginStatus.LOGIN;
            } else {
                user = User.createLineUser(lineUserId, email, displayName);
                user = userRepository.save(user);
                loginStatus = OauthLoginStatus.REGISTER;
            }
            String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole().name(), lineUserId);
            String tokenId = UUID.randomUUID().toString();
            String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getRole().name(), tokenId,
                    lineUserId);
            String redisKey = REFRESH_TOKEN_KEY_PREFIX + user.getId();
            redisUtil.setRefreshToken(redisKey, refreshToken, refreshTokenExpiration);

            return LoginResponse.of(accessToken, refreshToken, loginStatus, user.getId(), tokenId);
        } catch (StateValidationException ex) {
            log.warn("State 검증 실패: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.warn("LINE OAuth 로그인 처리 중 오류 발생", ex);
            throw new OauthException(ErrorMessage.OAUTH_ERROR);
        }
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtExtractor.extractJwtToken(request)
                .orElseThrow(TokenInvalidException::new);
        Long userId = jwtExtractor.getId(token);

        String redisKey = REFRESH_TOKEN_KEY_PREFIX + userId;
        redisTemplate.delete(redisKey);

        cookieUtil.deleteCookie(JwtProvider.ACCESS_TOKEN_HEADER, response);
        cookieUtil.deleteCookie(JwtProvider.REFRESH_TOKEN_HEADER, response);
    }

    public String generateLineLoginUrl() {
        String state = stateService.generateState();
        String redirectUri = URLEncoder.encode(props.getRedirectUri(), StandardCharsets.UTF_8);
        return AUTHORIZE_PATH +
                PARAM_RESPONSE_TYPE +
                PARAM_CLIENT_ID + props.getClientId() +
                PARAM_REDIRECT_URI + redirectUri +
                PARAM_STATE + state +
                PARAM_SCOPE +
                PARAM_UI_LOCALES;
    }


    @Transactional
    public LoginResponse loginWithGoogle(String code, String state) {
        try {
            GoogleTokenDto tokenResp = googleOAuthClient.issueToken(code);
            GoogleProfileDto profile = googleOAuthClient.fetchProfile(tokenResp.accessToken());

            String googleUserId = profile.userId();
            String email = profile.email();
            String displayName = profile.name();
            String firstName = profile.givenName();
            String lastName = profile.familyName();
            String profileImageUrl = profile.picture();

            Optional<User> userOpt = userRepository.findByGoogleId(googleUserId);
            User user;
            OauthLoginStatus loginStatus;

            if (userOpt.isPresent()) {
                user = userOpt.get();
                user.updateLastLoginAt();
                user.updateEmail(email);
                user.updateDisplayName(displayName);
                user.updateFirstName(firstName);
                user.updateLastName(lastName);

                if (user.getRole() == Role.PENDING) {
                    loginStatus = OauthLoginStatus.REGISTER;
                } else if (user.getRole() == Role.CUSTOMER) {
                    // Customer는 추가 정보 없이 바로 로그인
                    loginStatus = OauthLoginStatus.LOGIN;
                } else if (user.getRole() == Role.BRAND && !isBrandInfoCompleted(user)) {
                    loginStatus = OauthLoginStatus.INFO_REQUIRED;
                } else if (user.getRole() == Role.CREATOR && !isCreatorInfoCompleted(user)) {
                    loginStatus = getCreatorStatus(user);
                } else {
                    loginStatus = OauthLoginStatus.LOGIN;
                }
            } else {
                user = User.createGoogleUser(googleUserId, email, displayName, firstName, lastName, profileImageUrl);
                loginStatus = OauthLoginStatus.REGISTER;
            }

            user = userRepository.save(user);

            String accessToken = jwtProvider.generateGoogleAccessToken(user.getId(), user.getRole().name(),
                    googleUserId);
            String tokenId = UUID.randomUUID().toString();
            String refreshToken = jwtProvider.generateGoogleRefreshToken(user.getId(), user.getRole().name(), tokenId,
                    googleUserId);

            String redisKey = REFRESH_TOKEN_KEY_PREFIX + user.getId();
            redisUtil.setRefreshToken(redisKey, refreshToken, refreshTokenExpiration);

            return LoginResponse.withRole(accessToken, refreshToken, loginStatus, user.getId(), tokenId,
                    user.getRole());
        } catch (Exception ex) {
            throw new OauthException(ErrorMessage.OAUTH_ERROR);
        }
    }

    public String generateGoogleLoginUrl() {
        String state = stateService.generateState();
        String redirectUri = URLEncoder.encode(googleProps.redirectUri(), StandardCharsets.UTF_8);
        String scopes = googleProps.scope().replace(" ", "+");

        String url = GoogleConstants.AUTHORIZE_BASE_URL +
                GoogleConstants.PARAM_RESPONSE_TYPE +
                GoogleConstants.PARAM_CLIENT_ID + googleProps.clientId() +
                GoogleConstants.PARAM_REDIRECT_URI + redirectUri +
                GoogleConstants.PARAM_SCOPE + scopes +
                GoogleConstants.PARAM_STATE + state;

        return url;
    }


    @Transactional
    public RoleUpdateResponse updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (newRole == Role.PENDING || newRole == Role.ADMIN) {
            throw new InvalidRoleException();
        }

        if (!isInfoRequiredState(user) && user.getRole() != Role.PENDING) {
            throw new RoleChangeNotAllowedException();
        }

        OauthLoginStatus resultStatus;

        if (user.getRole() == Role.PENDING) {
            //  PENDING : 첫 역할 설정
            user.updateRole(newRole);
            createRoleEntity(user, newRole);
            resultStatus = (newRole == Role.CUSTOMER) ? OauthLoginStatus.LOGIN : OauthLoginStatus.INFO_REQUIRED;

        } else if (isInfoRequiredState(user)) {
            //  INFO_REQUIRED 상태에서 역할 변경 요청
            if (user.getRole() != newRole) {
                // 기존 역할과 다른 경우: 기존 엔티티 삭제 후 새로 생성
                deleteExistingRoleEntity(user);
                userRepository.flush();
                user.updateRole(newRole);
                createRoleEntity(user, newRole);
            }
            // 동일한 역할이든 다른 역할이든 INFO_REQUIRED 유지
            // 단, Customer는 제외
            if (newRole == Role.CUSTOMER) {
                resultStatus = OauthLoginStatus.LOGIN;
            } else if (newRole == Role.CREATOR) {
                // 기존 Creator의 진행 상태 확인
                resultStatus = getCreatorStatus(user);
            } else {
                resultStatus = OauthLoginStatus.INFO_REQUIRED;
            }
        } else {
            //  LOGIN : 이미 모든 정보 입력 완료
            // 새로운 역할 요청 무시, 기존 상태 유지
            resultStatus = OauthLoginStatus.LOGIN;
            newRole = user.getRole();
        }

        userRepository.save(user);

        String tokenId = UUID.randomUUID().toString();
        String accessToken = jwtProvider.generateGoogleAccessToken(userId, newRole.name(), user.getGoogleId());
        String refreshToken = jwtProvider.generateGoogleRefreshToken(userId, newRole.name(), tokenId,
                user.getGoogleId());

        String redisKey = REFRESH_TOKEN_KEY_PREFIX + userId;
        redisUtil.setRefreshToken(redisKey, refreshToken, refreshTokenExpiration);

        return new RoleUpdateResponse(accessToken, refreshToken, newRole, userId, resultStatus);
    }

    // Brand 추가 정보 완성 여부 확인
    private boolean isBrandInfoCompleted(User user) {
        Brand brand = user.getBrand();
        return brand != null &&
                brand.getBrandName() != null;
    }

    // Creator 추가 정보 완성 여부 확인
    private boolean isCreatorInfoCompleted(User user) {
        Creator creator = user.getCreator();
        if (creator == null) {
            return false;
        }

        // 1단계: 기본 정보 확인 (creatorName만 체크)
        boolean hasBasicInfo = creator.getCreatorName() != null;

        // 2단계: SNS 연동 확인 (최소 하나 이상)
        boolean hasSnsConnected = (creator.getInstagramUserId() != null) ||
                (creator.getTikTokUserId() != null);

        // 둘 다 충족해야 완료
        return hasBasicInfo && hasSnsConnected;
    }

    // 크리에이터 상태 확인
    public OauthLoginStatus getCreatorStatus(User user) {
        Creator creator = user.getCreator();

        boolean hasBasicInfo = creator.getCreatorName() != null;
        boolean hasSnsConnected = (creator.getInstagramUserId() != null) ||
                (creator.getTikTokUserId() != null);

        if (!hasBasicInfo) {
            return OauthLoginStatus.INFO_REQUIRED;
        } else if (!hasSnsConnected) {
            return OauthLoginStatus.SNS_REQUIRED;
        } else {
            return OauthLoginStatus.LOGIN;
        }
    }

    // INFO_REQUIRED 상태 확인
    private boolean isInfoRequiredState(User user) {
        return switch (user.getRole()) {
            // Customer는 추가 정보 없음
            case CUSTOMER -> false;
            case BRAND -> !isBrandInfoCompleted(user);
            case CREATOR -> !isCreatorInfoCompleted(user);
            default -> false;
        };
    }

    // 역할 변경 시 기존 역할 엔티티 삭제
    private void deleteExistingRoleEntity(User user) {
        switch (user.getRole()) {
            case CUSTOMER -> {
                if (user.getCustomer() != null) {
                    customerRepository.delete(user.getCustomer());
                    user.assignCustomer(null);
                }
            }
            case CREATOR -> {
                if (user.getCreator() != null) {
                    creatorRepository.delete(user.getCreator());
                    user.assignCreator(null);
                }
            }
            case BRAND -> {
                if (user.getBrand() != null) {
                    brandRepository.delete(user.getBrand());
                    user.assignBrand(null);
                }
            }
        }
    }

    // 역할별 엔티티 생성
    private void createRoleEntity(User user, Role role) {
        switch (role) {
            case CUSTOMER -> {
                Customer customer = Customer.builder()
                        .user(user)
                        .build();
                user.assignCustomer(customer);
            }
            case CREATOR -> {
                Creator creator = Creator.builder()
                        .user(user)
                        .build();
                user.assignCreator(creator);
            }
            case BRAND -> {
                Brand brand = Brand.builder()
                        .user(user)
                        .build();
                user.assignBrand(brand);
            }
        }
    }


}
