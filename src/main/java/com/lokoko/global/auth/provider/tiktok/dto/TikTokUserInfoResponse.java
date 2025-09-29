package com.lokoko.global.auth.provider.tiktok.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TikTokUserInfoResponse(
        TikTokDataWrapper data,
        TikTokErrorDto error
) {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record TikTokDataWrapper(
            TikTokProfileDto user
    ) {
    }
}