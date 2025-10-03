package com.lokoko.global.auth.provider.insta.config;

import com.lokoko.domain.user.application.service.UserGetService;
import com.lokoko.global.auth.exception.InstagramLongTokenRequestFailedException;
import com.lokoko.global.auth.exception.InstagramRefreshTokenFailedException;
import com.lokoko.global.auth.exception.InstagramTokenRequestFailedException;
import com.lokoko.global.auth.provider.insta.dto.*;
import com.lokoko.global.auth.service.OAuthStateManager;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
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
    private final WebClient instagramWebClient;
    private final InstaProperties props;
    private final UserGetService userGetService;

    /**
     * Instagram Graph API를 위한 Facebook OAuth URL 생성
     * Facebook 로그인을 통해 페이지 및 Instagram 비즈니스 계정 접근 권한 요청
     */
    public String buildAuthorizationUrl(Long userId, String returnTo) {
        String state = oAuthStateManager.generateStateWithReturnTo(userId, returnTo);

        String encodedRedirect = URLEncoder.encode(props.redirectUri(), StandardCharsets.UTF_8);

        // Instagram Graph API에 필요한 Facebook 권한 스코프
        String scope = String.join(",",
                InstagramConstants.SCOPE_PAGES_SHOW_LIST,
                InstagramConstants.SCOPE_PAGES_READ_ENGAGEMENT,
                InstagramConstants.SCOPE_INSTAGRAM_BASIC,
                InstagramConstants.SCOPE_INSTAGRAM_MANAGE_INSIGHTS,
                InstagramConstants.SCOPE_BUSINESS_MANAGEMENT
        );
        String encodedScope = URLEncoder.encode(scope, StandardCharsets.UTF_8);

        return InstagramConstants.FACEBOOK_AUTHORIZE_URL
                + "?"
                + InstagramConstants.PARAM_CLIENT_ID + "=" + props.clientId()
                + "&" + InstagramConstants.PARAM_REDIRECT_URI + "=" + encodedRedirect
                + "&" + InstagramConstants.PARAM_RESPONSE_TYPE + "=" + InstagramConstants.RESPONSE_TYPE_CODE
                + "&" + InstagramConstants.PARAM_SCOPE + "=" + encodedScope
                + "&" + InstagramConstants.PARAM_STATE + "=" + state;
    }

    /**
     * Facebook OAuth code를 액세스 토큰으로 교환
     */
    public FacebookAccessTokenDto exchangeFacebookToken(String code) {
        try {
            FacebookAccessTokenDto dto = instagramWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("graph.facebook.com")
                            .path("/v23.0/oauth/access_token")
                            .queryParam(InstagramConstants.PARAM_CLIENT_ID, props.clientId())
                            .queryParam(InstagramConstants.PARAM_CLIENT_SECRET, props.clientSecret())
                            .queryParam(InstagramConstants.PARAM_REDIRECT_URI, props.redirectUri())
                            .queryParam(InstagramConstants.PARAM_CODE, code)
                            .build())
                    .retrieve()
                    .bodyToMono(FacebookAccessTokenDto.class)
                    .block();

            if (dto == null || dto.accessToken() == null) {
                throw new InstagramTokenRequestFailedException();
            }

            return dto;
        } catch (Exception e) {
            log.error("Facebook 토큰 발급 실패: {}", e.getMessage(), e);
            throw new InstagramTokenRequestFailedException();
        }
    }

    /**
     * Facebook 페이지 목록 조회
     * 각 페이지에 연결된 Instagram 비즈니스 계정을 찾기 위해 필요
     */
    public FacebookPagesDto getFacebookPages(String accessToken) {
        try {
            FacebookPagesDto dto = instagramWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("graph.facebook.com")
                            .path("/v23.0/me/accounts")
                            .queryParam(InstagramConstants.PARAM_FIELDS, "id,name,access_token,category,tasks")
                            .queryParam(InstagramConstants.PARAM_ACCESS_TOKEN, accessToken)
                            .build())
                    .retrieve()
                    .bodyToMono(FacebookPagesDto.class)
                    .block();

            if (dto == null || dto.data() == null || dto.data().isEmpty()) {
                log.error("연동된 Facebook 페이지가 없습니다");
                throw new InstagramTokenRequestFailedException();
            }

            return dto;
        } catch (Exception e) {
            log.error("Facebook 페이지 조회 실패: {}", e.getMessage(), e);
            throw new InstagramTokenRequestFailedException();
        }
    }

    /**
     * Facebook 페이지에 연결된 Instagram 비즈니스 계정 조회
     */
    public InstagramBusinessAccountDto getInstagramBusinessAccount(String pageId, String pageAccessToken) {
        try {
            InstagramBusinessAccountDto dto = instagramWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("graph.facebook.com")
                            .path("/v23.0/" + pageId)
                            .queryParam(InstagramConstants.PARAM_FIELDS, "instagram_business_account{id,username}")
                            .queryParam(InstagramConstants.PARAM_ACCESS_TOKEN, pageAccessToken)
                            .build())
                    .retrieve()
                    .bodyToMono(InstagramBusinessAccountDto.class)
                    .block();

            if (dto == null || dto.instagramBusinessAccount() == null) {
                log.warn("페이지 {}에 연결된 Instagram 비즈니스 계정이 없습니다", pageId);
                return null;
            }

            return dto;
        } catch (Exception e) {
            log.error("Instagram 비즈니스 계정 조회 실패 (pageId: {}): {}", pageId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Instagram 비즈니스 계정의 장기 액세스 토큰 발급
     */
    public InstagramGraphTokenDto exchangeLongLivedToken(String shortAccessToken) {
        try {
            InstagramGraphTokenDto dto = instagramWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("graph.facebook.com")
                            .path("/v23.0/oauth/access_token")
                            .queryParam(InstagramConstants.PARAM_GRANT_TYPE, "fb_exchange_token")
                            .queryParam("fb_exchange_token", shortAccessToken)
                            .queryParam(InstagramConstants.PARAM_CLIENT_ID, props.clientId())
                            .queryParam(InstagramConstants.PARAM_CLIENT_SECRET, props.clientSecret())
                            .build())
                    .retrieve()
                    .bodyToMono(InstagramGraphTokenDto.class)
                    .block();

            if (dto == null || dto.accessToken() == null) {
                throw new InstagramLongTokenRequestFailedException();
            }
            return dto;
        } catch (Exception e) {
            log.error("Instagram Graph API 장기 토큰 발급 실패: {}", e.getMessage(), e);
            throw new InstagramLongTokenRequestFailedException();
        }
    }

    // ========== 아래는 Basic Display API 방식 (Deprecated) ==========

    /**
     * @deprecated Instagram Basic Display API 방식 (더 이상 사용하지 않음)
     * code → 단기(Short-lived) 액세스 토큰 교환
     */
    @Deprecated
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
     * @deprecated Instagram Basic Display API 방식 (더 이상 사용하지 않음)
     * 단기 토큰 → 장기(Long-lived) 토큰 교환
     */
    @Deprecated
    public InstagramLongTokenDto exchangeLongLivedTokenBasicDisplay(String shortAccessToken) {
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
     * @deprecated Instagram Basic Display API 방식 (더 이상 사용하지 않음)
     * 장기 토큰 갱신
     */
    @Deprecated
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
