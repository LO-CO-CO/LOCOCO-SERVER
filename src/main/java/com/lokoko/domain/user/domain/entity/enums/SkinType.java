package com.lokoko.domain.user.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SkinType {
    DRY("건성"),
    OILY("지성"),
    COMBINATION("복합성"),
    ATOPIC("아토피성"),
    SENSITIVE("민감성"),
    OTHER("기타");

    private final String displayName;
}
