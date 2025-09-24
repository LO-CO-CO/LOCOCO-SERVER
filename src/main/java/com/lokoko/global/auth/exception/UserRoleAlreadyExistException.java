package com.lokoko.global.auth.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserRoleAlreadyExistException extends BaseException {

    public UserRoleAlreadyExistException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.ROLE_ALREADY_EXIST.getMessage());
    }
}
