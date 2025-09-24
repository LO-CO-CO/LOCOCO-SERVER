package com.lokoko.domain.creator.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentLanguage {
    ENGLISH("영어"),
    SPANISH("스페인어"),
    ENGLISH_AND_SPANISH("영어랑 스페인어");

    private final String displayName;
}