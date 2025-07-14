package com.lokoko.domain.product.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MissingProductImageException extends BaseException {
    public MissingProductImageException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.PRODUCT_IMAGE_NOT_FOUND.getMessage());
    }
}
