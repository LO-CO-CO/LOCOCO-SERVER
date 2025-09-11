package com.lokoko.domain.creator.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CreatorType {
    NORMAL("일반 회원"),
    PRO("Pro");

    private final String displayName;
}