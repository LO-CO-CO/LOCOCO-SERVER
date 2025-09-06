package com.lokoko.domain.creator.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CreatorStatus {
    NOT_APPROVED("미승인"),
    APPROVED("승인");

    private final String displayName;
}