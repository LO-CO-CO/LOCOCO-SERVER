package com.lokoko.domain.media.image.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    FILE_TYPE_NOT_SUPPORTED("지원하지 않는 파일 형식입니다."),
    PRODUCT_IMAGE_COUNT_NOT_VALID("상품 이미지는 1개에서 5개 사이여야 합니다.");

    private final String message;
}
