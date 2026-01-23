package com.lokoko.domain.product.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductCategory {

    ESSENCE_TONER("에센스/토너"),
    SERUM_AMPOULE("세럼/앰플"),
    CREAM_LOTION("크림/로션"),
    CLEANSER("클렌저"),
    SUNCARE("선케어"),
    ETC("기타");

    private final String displayName;
}