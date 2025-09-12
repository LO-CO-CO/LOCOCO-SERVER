package com.lokoko.global.auth.tiktok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TikTokProfileDto(
        @JsonProperty("open_id")
        String openId,
        @JsonProperty("union_id")
        String unionId,
        @JsonProperty("follower_count")
        Long followerCount,
        @JsonProperty("following_count")
        Long followingCount,
        @JsonProperty("likes_count")
        Long likesCount,
        @JsonProperty("video_count")
        Long videoCount
) {
}