package com.lokoko.domain.user.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AdminNotFoundException extends BaseException {
    public AdminNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.ADMIN_NOT_FOUND.getMessage());
    }
}
