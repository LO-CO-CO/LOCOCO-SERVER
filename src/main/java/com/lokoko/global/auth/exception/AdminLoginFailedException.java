package com.lokoko.global.auth.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AdminLoginFailedException extends BaseException {

    public AdminLoginFailedException() {
        super(HttpStatus.UNAUTHORIZED, ErrorMessage.ADMIN_LOGIN_FAILED.getMessage());
    }
}