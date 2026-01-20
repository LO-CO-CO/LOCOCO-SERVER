package com.lokoko.domain.productBrand.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProductBrandNotFoundException extends BaseException {
    public ProductBrandNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.PRODUCT_BRAND_NOT_FOUND.getMessage());
    }
}
