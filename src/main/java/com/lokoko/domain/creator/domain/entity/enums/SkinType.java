package com.lokoko.domain.creator.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SkinType {
    NORMAL("기본"),
    DRY("건성"),
    OILY("지성"),
    COMBINATION("복합성"),
    SENSITIVE("민감성"),
    OTHER("기타");

    private final String displayName;
}
