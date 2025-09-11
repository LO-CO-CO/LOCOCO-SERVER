package com.lokoko.domain.brand.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BrandNotFoundException extends BaseException {
    public BrandNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.BRAND_NOT_FOUND.getMessage());
    }
}
