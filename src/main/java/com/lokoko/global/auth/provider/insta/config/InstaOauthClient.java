package com.lokoko.global.auth.provider.insta.config;

import com.lokoko.domain.user.application.service.UserGetService;
import com.lokoko.global.auth.exception.InstagramLongTokenRequestFailedException;
import com.lokoko.global.auth.exception.InstagramRefreshTokenFailedException;
import com.lokoko.global.auth.exception.InstagramTokenRequestFailedException;
import com.lokoko.global.auth.provider.insta.dto.InstagramLongTokenDto;
import com.lokoko.global.auth.provider.insta.dto.InstagramShortTokenDto;
import com.lokoko.global.auth.service.OAuthStateManager;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class InstaOauthClient {

    private final OAuthStateManager oAuthStateManager;
    private final @Qualifier("instagramWebClient") WebClient instagramWebClient;
    private final InstaProperties props;
    private final UserGetService userGetService;

    public String buildAuthorizationUrl(Long userId) {
        String state = oAuthStateManager.generateState(userId);

        String encodedRedirect = URLEncoder.encode(props.redirectUri(), StandardCharsets.UTF_8);
        String encodedScope = URLEncoder.encode(props.scope(), StandardCharsets.UTF_8);

        return InstagramConstants.AUTHORIZE_BASE_URL
                + "?"
                + InstagramConstants.PARAM_CLIENT_ID + "=" + props.clientId()
                + "&" + InstagramConstants.PARAM_REDIRECT_URI + "=" + encodedRedirect
                + "&" + InstagramConstants.PARAM_RESPONSE_TYPE + "=" + InstagramConstants.RESPONSE_TYPE_CODE
                + "&" + InstagramConstants.PARAM_SCOPE + "=" + encodedScope
                + "&" + InstagramConstants.PARAM_STATE + "=" + state;
    }

    /**
     * code → 단기(Short-lived) 액세스 토큰 교환
     */
    public InstagramShortTokenDto exchangeShortLivedToken(String code) {
        try {
            InstagramShortTokenDto dto = instagramWebClient.post()
                    .uri(InstagramConstants.TOKEN_URL)
                    .body(BodyInserters.fromFormData(InstagramConstants.PARAM_CLIENT_ID, props.clientId())
                            .with(InstagramConstants.PARAM_CLIENT_SECRET, props.clientSecret())
                            .with(InstagramConstants.PARAM_GRANT_TYPE, "authorization_code")
                            .with(InstagramConstants.PARAM_REDIRECT_URI, props.redirectUri())
                            .with(InstagramConstants.PARAM_CODE, code))
                    .retrieve()
                    .bodyToMono(InstagramShortTokenDto.class)
                    .block();

            if (dto == null || dto.accessToken() == null) {
                throw new InstagramTokenRequestFailedException();
            }

            return dto;
        } catch (Exception e) {
            log.error("Instagram 단기 토큰 발급 실패: {}", e.getMessage(), e);
            throw new InstagramTokenRequestFailedException();
        }
    }

    /**
     * 단기 토큰 → 장기(Long-lived) 토큰 교환
     */
    public InstagramLongTokenDto exchangeLongLivedToken(String shortAccessToken) {
        try {
            InstagramLongTokenDto dto = instagramWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("graph.instagram.com")
                            .path("/access_token")
                            .queryParam(InstagramConstants.PARAM_GRANT_TYPE,
                                    InstagramConstants.GRANT_TYPE_IG_EXCHANGE_TOKEN)
                            .queryParam(InstagramConstants.PARAM_CLIENT_SECRET, props.clientSecret())
                            .queryParam(InstagramConstants.PARAM_ACCESS_TOKEN, shortAccessToken)
                            .build())
                    .retrieve()
                    .bodyToMono(InstagramLongTokenDto.class)
                    .block();

            if (dto == null || dto.accessToken() == null) {
                throw new InstagramLongTokenRequestFailedException();
            }
            return dto;
        } catch (Exception e) {
            log.error("Instagram 장기 토큰 발급 실패: {}", e.getMessage(), e);
            throw new InstagramLongTokenRequestFailedException();
        }
    }

    /**
     * 장기 토큰 갱신
     */
    public InstagramLongTokenDto refreshLongLivedToken(String longAccessToken) {
        try {
            InstagramLongTokenDto dto = instagramWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("graph.instagram.com")
                            .path("/refresh_access_token")
                            .queryParam(InstagramConstants.PARAM_GRANT_TYPE,
                                    InstagramConstants.GRANT_TYPE_IG_REFRESH_TOKEN)
                            .queryParam(InstagramConstants.PARAM_ACCESS_TOKEN, longAccessToken)
                            .build())
                    .retrieve()
                    .bodyToMono(InstagramLongTokenDto.class)
                    .block();

            if (dto == null || dto.accessToken() == null) {
                throw new InstagramRefreshTokenFailedException();
            }
            return dto;
        } catch (Exception e) {
            log.error("Instagram 장기 토큰 갱신 실패: {}", e.getMessage(), e);
            throw new InstagramRefreshTokenFailedException();
        }
    }
}
