package com.lokoko.global.auth.service;

import static com.lokoko.global.auth.jwt.utils.JwtProvider.EMAIL_CLAIM;
import static com.lokoko.global.utils.LineConstants.AUTHORIZE_PATH;
import static com.lokoko.global.utils.LineConstants.PARAM_CLIENT_ID;
import static com.lokoko.global.utils.LineConstants.PARAM_REDIRECT_URI;
import static com.lokoko.global.utils.LineConstants.PARAM_RESPONSE_TYPE;
import static com.lokoko.global.utils.LineConstants.PARAM_SCOPE;
import static com.lokoko.global.utils.LineConstants.PARAM_STATE;
import static com.lokoko.global.utils.LineConstants.PARAM_UI_LOCALES;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.repository.UserRepository;
import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import com.lokoko.global.auth.exception.ErrorMessage;
import com.lokoko.global.auth.exception.OauthException;
import com.lokoko.global.auth.exception.StateValidationException;
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
import com.lokoko.global.utils.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final StateService stateService;
    private final LineOAuthClient oAuthClient;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final JwtExtractor jwtExtractor;
    private final LineProperties props;
    private final RedisUtil redisUtil;
    private final CookieUtil cookieUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_KEY_PREFIX = "refreshToken:";

    @Value("${lokoko.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    @Transactional
    public LoginResponse loginWithLine(String code, String state, String redirectUri) {
        try {
            LineTokenDto tokenResp = oAuthClient.issueToken(code, redirectUri);
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
            log.error("LINE OAuth 로그인 처리 중 오류 발생", ex);
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

        cookieUtil.deleteCookie(response, JwtProvider.ACCESS_TOKEN_HEADER);
        cookieUtil.deleteCookie(response, JwtProvider.REFRESH_TOKEN_HEADER);
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
}
