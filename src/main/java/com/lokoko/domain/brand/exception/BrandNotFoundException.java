package com.lokoko.domain.brand.exception;

import com.lokoko.domain.brand.exception.ErrorMessage;
import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BrandNotFoundException extends BaseException {
    public BrandNotFoundException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.BRAND_NOT_FOUND.getMessage());
    }
}