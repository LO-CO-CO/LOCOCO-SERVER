package com.lokoko.domain.socialclip.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialClipContent {

    Reels("릴스"),
    Shorts("쇼츠");

    private final String displayName;
}
