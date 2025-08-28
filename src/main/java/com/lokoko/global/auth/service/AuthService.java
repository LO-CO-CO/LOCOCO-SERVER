package com.lokoko.global.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lokoko.domain.customer.domain.entity.Customer;
import com.lokoko.domain.customer.domain.repository.CustomerRepository;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import com.lokoko.global.auth.exception.ErrorMessage;
import com.lokoko.global.auth.exception.OauthException;
import com.lokoko.global.auth.exception.StateValidationException;
import com.lokoko.global.auth.google.GoogleOAuthClient;
import com.lokoko.global.auth.google.GoogleProperties;
import com.lokoko.global.auth.google.dto.GoogleProfileDto;
import com.lokoko.global.auth.google.dto.GoogleTokenDto;
import com.lokoko.global.auth.jwt.dto.LoginResponse;
import com.lokoko.global.auth.jwt.exception.TokenInvalidException;
import com.lokoko.global.auth.jwt.utils.CookieUtil;
import com.lokoko.global.auth.jwt.utils.JwtExtractor;
import com.lokoko.global.auth.jwt.utils.JwtProvider;
import com.lokoko.global.auth.line.LineOAuthClient;
import com.lokoko.global.auth.line.LineProperties;
import com.lokoko.global.auth.line.dto.LineProfileDto;
import com.lokoko.global.auth.line.dto.LineTokenDto;
import com.lokoko.global.auth.line.dto.LineUserInfoDto;
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

            Optional<Customer> userOpt = customerRepository.findByLineId(lineUserId);
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
                user = Customer.createLineUser(lineUserId, email, displayName);
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

            Optional<Customer> userOpt = customerRepository.findByGoogleId(googleUserId);
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
                user = Customer.createGoogleUser(googleUserId, email, displayName);
                user = userRepository.save(user);
                loginStatus = OauthLoginStatus.REGISTER;
            }

            String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole().name(), googleUserId);

            String tokenId = UUID.randomUUID().toString();
            String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getRole().name(), tokenId, googleUserId);

            String redisKey = REFRESH_TOKEN_KEY_PREFIX + user.getId();
            redisUtil.setRefreshToken(redisKey, refreshToken, refreshTokenExpiration);

            LoginResponse response = LoginResponse.of(accessToken, refreshToken, loginStatus, user.getId(), tokenId);

            return response;
        } catch (Exception ex) {
            throw new OauthException(ErrorMessage.OAUTH_ERROR);
        }
    }

    public String generateGoogleLoginUrl() {
        String state = stateService.generateState();
        String redirectUri = URLEncoder.encode(googleProps.getRedirectUri(), StandardCharsets.UTF_8);
        String scopes = googleProps.getScope().replace(" ", "+");

        String url = GoogleConstants.AUTHORIZE_BASE_URL +
                GoogleConstants.PARAM_RESPONSE_TYPE +
                GoogleConstants.PARAM_CLIENT_ID + googleProps.getClientId() +
                GoogleConstants.PARAM_REDIRECT_URI + redirectUri +
                GoogleConstants.PARAM_SCOPE + scopes +
                GoogleConstants.PARAM_STATE + state;

        return url;
    }

}
