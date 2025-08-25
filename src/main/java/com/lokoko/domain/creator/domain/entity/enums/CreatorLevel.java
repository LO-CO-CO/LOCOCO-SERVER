package com.lokoko.domain.creator.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CreatorLevel {
    //아직 레벨의 개수는 논의중이라서 일단 2개만 넣었습니다.

    LEVEL_1("Lv.1"),
    LEVEL_2("Lv.2");

    private final String displayName;
}
