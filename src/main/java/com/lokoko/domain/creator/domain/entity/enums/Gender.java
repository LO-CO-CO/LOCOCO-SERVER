package com.lokoko.domain.creator.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("남성"),
    FEMALE("여성"),
    NON_BINARY("논바이너리"),
    PREFER_NOT_TO_SAY("밝히고 싶지 않음");

    private final String displayName;
}
