package com.lokoko.domain.socialclip.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {

    INSTA_REELS("인스타 릴스"),
    TIKTOK_VIDEO("틱톡 비디오"),
    INSTA_POST("인스타 게시물");

    private final String displayName;
}
