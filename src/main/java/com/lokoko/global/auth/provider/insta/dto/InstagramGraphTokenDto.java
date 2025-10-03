package com.lokoko.global.auth.provider.insta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Instagram Graph API 장기 토큰 DTO
 */
public record InstagramGraphTokenDto(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        Long expiresIn
) {
}
