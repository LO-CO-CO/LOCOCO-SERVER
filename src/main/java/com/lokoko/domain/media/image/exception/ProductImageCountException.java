package com.lokoko.domain.media.image.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;


public class ProductImageCountException extends BaseException {
    public ProductImageCountException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_IMAGE_COUNT_NOT_VALID.getMessage());
    }
}
