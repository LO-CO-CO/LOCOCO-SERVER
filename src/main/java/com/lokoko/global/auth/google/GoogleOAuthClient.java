package com.lokoko.global.auth.google;

import com.lokoko.global.auth.google.dto.GoogleProfileDto;
import com.lokoko.global.auth.google.dto.GoogleTokenDto;
import com.lokoko.global.utils.GoogleConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class GoogleOAuthClient {
    private final WebClient googleWebClient;
    private final GoogleProperties props;
    private final WebClient googleUserInfoWebClient;

    public GoogleTokenDto issueToken(String code) {
        return googleWebClient.post()
                .uri(GoogleConstants.TOKEN_PATH)
                .body(BodyInserters.fromFormData(GoogleConstants.PARAM_GRANT_TYPE, GoogleConstants.GRANT_TYPE_AUTH_CODE)
                        .with(GoogleConstants.PARAM_CODE, code)
                        .with(GoogleConstants.REDIRECT_URI, props.getRedirectUri())
                        .with(GoogleConstants.CLIENT_ID, props.getClientId())
                        .with(GoogleConstants.PARAM_CLIENT_SECRET, props.getClientSecret()))
                .retrieve()
                .bodyToMono(GoogleTokenDto.class)
                .block();
    }


    public GoogleProfileDto fetchProfile(String accessToken) {
        return googleUserInfoWebClient
                .get()
                .uri(GoogleConstants.USERINFO_PATH)
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(GoogleProfileDto.class)
                .block();
    }


}