package com.lokoko.global.auth.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotCompletedSingUpException extends BaseException {

    public UserNotCompletedSingUpException() {
        super(HttpStatus.FORBIDDEN, ErrorMessage.USER_NOT_COMPLETED_SIGN_UP.getMessage());
    }
}
