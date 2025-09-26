package com.lokoko.global.auth.provider.tiktok.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TikTokProfileDto(
        String openId,
        String unionId,
        Long followerCount,
        Long followingCount,
        Long likesCount,
        Long videoCount
) {
}