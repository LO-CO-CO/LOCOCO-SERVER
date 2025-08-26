package com.lokoko.domain.user.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    CUSTOMER("일반 유저"),
    CREATOR("크리에이터"),
    BRAND("브랜드"),
    ADMIN("관리자");

    private final String displayName;
}
