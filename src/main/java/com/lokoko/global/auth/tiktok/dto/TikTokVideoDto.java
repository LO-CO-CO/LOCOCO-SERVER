package com.lokoko.global.auth.tiktok.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TikTokVideoDto(
        String id,
        Long createTime,
        Long viewCount,
        Integer likeCount,
        Integer commentCount,
        Integer shareCount,
        String coverImageUrl,
        String videoDescription,
        Integer duration,
        Integer height,
        Integer width,
        String title,
        String embedLink,
        String shareUrl
) {
}