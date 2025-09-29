package com.lokoko.global.auth.provider.tiktok.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TikTokVideoListResponse(
        TikTokVideoDataWrapper data,
        TikTokErrorDto error
) {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record TikTokVideoDataWrapper(
            List<TikTokVideoDto> videos,
            Long cursor,
            boolean hasMore
    ) {
    }
}