package com.lokoko.global.auth.provider.insta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InstagramLongTokenDto(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("token_type")
        String tokenType,
        
        @JsonProperty("expires_in")
        Long expiresIn
) {
}
