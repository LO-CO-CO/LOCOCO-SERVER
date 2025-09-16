package com.lokoko.domain.customer.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;


public class CustomerNotFoundException extends BaseException {
    public CustomerNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.CUSTOMER_NOT_FOUND.getMessage());
    }
}
