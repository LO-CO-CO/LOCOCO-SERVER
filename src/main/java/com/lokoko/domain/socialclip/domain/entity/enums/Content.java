package com.lokoko.domain.socialclip.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Content {

    REELS("릴스"),
    VIDEO("비디오"),
    POST("게시물");

    private final String displayName;
}