package com.lokoko.global.auth.provider.tiktok.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TikTokVideoDto(
        String id,
        Long createTime,
        Long viewCount,
        Long likeCount,
        Long commentCount,
        Long shareCount,
        String shareUrl
) {
}