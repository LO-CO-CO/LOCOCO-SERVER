package com.lokoko.domain.productBrand.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    PRODUCT_BRAND_NOT_FOUND("존재하지 않는 상품 브랜드입니다.");

    private final String message;
}
