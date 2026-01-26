package com.lokoko.domain.productBrand.api.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
	PRODUCT_BRAND_GET_SUCCESS("상품 브랜드 조회에 성공했습니다."),
	PRODUCT_LIST_FETCH_SUCCESS("브랜드 이름으로 상품 목록 조회에 성공했습니다.");

	private final String message;
}
