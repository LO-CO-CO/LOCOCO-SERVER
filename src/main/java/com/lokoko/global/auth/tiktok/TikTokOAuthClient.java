package com.lokoko.global.auth.tiktok;

import com.lokoko.global.auth.exception.TikTokProfileFetchFailedException;
import com.lokoko.global.auth.exception.TikTokTokenRequestFailedException;
import com.lokoko.global.auth.service.OAuthStateManager;
import com.lokoko.global.auth.tiktok.dto.TikTokProfileDto;
import com.lokoko.global.auth.tiktok.dto.TikTokTokenDto;
import com.lokoko.global.auth.tiktok.dto.TikTokUserInfoResponse;
import com.lokoko.global.auth.tiktok.dto.TikTokVideoListResponse;
import com.lokoko.global.utils.TikTokConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            return response.data().user();
        }

        throw new TikTokProfileFetchFailedException();
    }
    
    public TikTokTokenDto refreshToken(String refreshToken) {

        TikTokTokenDto tokenDto = tikTokWebClient.post()
                .uri(TikTokConstants.TOKEN_PATH)
                .body(BodyInserters.fromFormData("grant_type", "refresh_token")
                        .with("refresh_token", refreshToken)
                        .with(TikTokConstants.CLIENT_KEY, props.clientKey())
                        .with(TikTokConstants.PARAM_CLIENT_SECRET, props.clientSecret()))
                .retrieve()
                .bodyToMono(TikTokTokenDto.class)
                .block();
        
        if (tokenDto == null || tokenDto.accessToken() == null) {
            throw new TikTokTokenRequestFailedException();
        }
        return tokenDto;
    }
    
    /**
     * TikTok 특정 영상들 조회 (video ID 기반)
     */
    public TikTokVideoListResponse queryVideosByIds(String accessToken, Long videoId) {

        Map<String, Object> requestBody = createVideoQueryRequestBody(videoId);

        TikTokVideoListResponse response = tikTokWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(TikTokConstants.VIDEO_QUERY_PATH)
                        .queryParam(TikTokConstants.PARAM_FIELDS, TikTokConstants.VIDEO_FIELDS)
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(TikTokVideoListResponse.class)
                .block();

        if (response != null && response.data() != null) {
            return response;
        }

        throw new TikTokProfileFetchFailedException();
    }

    private Map<String, Object> createVideoQueryRequestBody(Long videoId) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("video_ids", List.of(videoId.toString()));

        Map<String, Object> body = new HashMap<>();
        body.put("filters", filters);

        return body;
    }
}