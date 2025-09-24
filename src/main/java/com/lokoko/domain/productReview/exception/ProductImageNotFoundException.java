package com.lokoko.domain.productReview.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProductImageNotFoundException extends BaseException {
    public ProductImageNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.PRODUCT_IMAGE_NOT_FOUND.getMessage());
    }
}
