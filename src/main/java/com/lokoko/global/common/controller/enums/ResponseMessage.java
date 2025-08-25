package com.lokoko.global.common.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    PRODUCT_TRANSLATION_SUCCESS("Product 테이블의 번역 마이그레이션에 성공하였습니다."),
    TRANSLATION_VALIDATION_SUCCESS("Product 테이블의 번역 검증에 성공하였습니다.");

    private final String message;

}
