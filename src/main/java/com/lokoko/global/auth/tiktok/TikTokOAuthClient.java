package com.lokoko.global.auth.tiktok;

import com.lokoko.global.auth.exception.TikTokProfileFetchFailedException;
import com.lokoko.global.auth.exception.TikTokTokenRequestFailedException;
import com.lokoko.global.auth.service.OAuthStateManager;
import com.lokoko.global.auth.tiktok.dto.TikTokProfileDto;
import com.lokoko.global.auth.tiktok.dto.TikTokTokenDto;
import com.lokoko.global.auth.tiktok.dto.TikTokUserInfoResponse;
import com.lokoko.global.utils.TikTokConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class TikTokOAuthClient {

    private final OAuthStateManager oAuthStateManager;
    private final WebClient tikTokWebClient;
    private final TikTokProperties props;


    public String buildAuthorizationUrl(Long creatorId) {

        String encodedRedirectUri = URLEncoder.encode(props.redirectUri(), StandardCharsets.UTF_8);
        String encodedScope = URLEncoder.encode(props.scope(), StandardCharsets.UTF_8);
        String state = oAuthStateManager.generateState(creatorId);

        String authUrl = TikTokConstants.AUTHORIZE_BASE_URL +
                TikTokConstants.PARAM_RESPONSE_TYPE +
                TikTokConstants.PARAM_CLIENT_KEY + props.clientKey() +
                TikTokConstants.PARAM_REDIRECT_URI + encodedRedirectUri +
                TikTokConstants.PARAM_SCOPE + encodedScope +
                "&state=" + state;

        log.info("TikTok Authorization URL generated: {}", authUrl);
        log.info("Redirect URI (encoded): {}", encodedRedirectUri);
        log.info("Redirect URI (original): {}", props.redirectUri());
        log.info("State parameter (creatorId): {}", state);

        return authUrl;
    }

    public TikTokTokenDto issueToken(String code) {

        TikTokTokenDto tokenDto = tikTokWebClient.post()
                .uri(TikTokConstants.TOKEN_PATH)
                .body(BodyInserters.fromFormData(TikTokConstants.PARAM_GRANT_TYPE, TikTokConstants.GRANT_TYPE_AUTH_CODE)
                        .with(TikTokConstants.PARAM_CODE, code)
                        .with(TikTokConstants.REDIRECT_URI, props.redirectUri())
                        .with(TikTokConstants.CLIENT_KEY, props.clientKey())
                        .with(TikTokConstants.PARAM_CLIENT_SECRET, props.clientSecret()))
                .retrieve()
                .bodyToMono(TikTokTokenDto.class)
                .block();

        if (tokenDto == null || tokenDto.accessToken() == null){
            throw new TikTokTokenRequestFailedException();
        }

        return tokenDto;
    }

    public TikTokProfileDto fetchProfile(String accessToken) {

        log.info("Request URL: {}", props.baseUrl() + TikTokConstants.USER_INFO_PATH + "?fields=" + TikTokConstants.DEFAULT_FIELDS);

        TikTokUserInfoResponse response = tikTokWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TikTokConstants.USER_INFO_PATH)
                        .queryParam(TikTokConstants.PARAM_FIELDS, TikTokConstants.DEFAULT_FIELDS)
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(TikTokUserInfoResponse.class)
                .block();

        if (response != null && response.data() != null && response.data().user() != null) {
            log.info("틱톡 유저 프로필 조회에 성공했습니다 . 틱톡 ID : {}", response.data().user().openId());
            return response.data().user();
        }

        throw new TikTokProfileFetchFailedException();
    }
}