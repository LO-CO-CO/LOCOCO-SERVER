package com.lokoko.global.auth.provider.insta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record InstagramShortTokenDto(
        @JsonProperty("access_token")
        String accessToken,
        
        @JsonProperty("user_id")
        Long userId,

        List<String> permissions
) {
}
