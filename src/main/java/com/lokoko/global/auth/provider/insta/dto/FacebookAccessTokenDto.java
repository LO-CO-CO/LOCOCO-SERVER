package com.lokoko.global.auth.provider.insta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Facebook OAuth 액세스 토큰 응답 DTO
 * Instagram Graph API 사용을 위해 먼저 Facebook 로그인이 필요
 */
public record FacebookAccessTokenDto(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        Long expiresIn
) {
}
