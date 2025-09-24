package com.lokoko.global.common.exception.translation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    MIGRATION_FAILED("번역 테이블 마이그레이션에 실패하였습니다");

    private final String message;

}
