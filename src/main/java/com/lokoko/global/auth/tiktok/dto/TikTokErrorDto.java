package com.lokoko.global.auth.tiktok.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TikTokErrorDto(
        String code,
        String message,
        String logId
) {
}