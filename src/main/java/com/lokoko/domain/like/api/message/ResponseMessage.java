package com.lokoko.domain.like.api.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    PRODUCT_LIKE_TOGGLE_SUCCESS("상품 좋아요 토글에 성공했습니다."),
    REVIEW_LIKE_TOGGLE_SUCCESS("리뷰 좋아요 토글에 성공했습니다.");

    private final String message;
}
