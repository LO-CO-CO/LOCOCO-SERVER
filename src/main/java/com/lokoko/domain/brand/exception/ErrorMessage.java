package com.lokoko.domain.brand.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    BRAND_NOT_FOUND("존재하지 않는 브랜드입니다.");

    private final String message;
}
