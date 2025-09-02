package com.lokoko.global.auth.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OauthLoginStatus {
    LOGIN("소셜 로그인 최종 완료"),
    INFO_REQUIRED("추가 정보 입력 필요"),
    REGISTER("최초 소셜 회원가입");

    private final String displayName;
}
