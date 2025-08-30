package com.lokoko.global.auth.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidRoleException extends BaseException {

    public InvalidRoleException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.ROLE_INVALID_TYPE.getMessage());
    }
}
