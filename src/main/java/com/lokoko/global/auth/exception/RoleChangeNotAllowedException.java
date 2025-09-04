package com.lokoko.global.auth.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RoleChangeNotAllowedException extends BaseException {

    public RoleChangeNotAllowedException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.ROLE_TRANSITION_NOT_ALLOWED_AFTER_LOGIN.getMessage());
    }
}
