package com.lokoko.global.auth.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotCompletedSignUpException extends BaseException {

    public UserNotCompletedSignUpException() {
        super(HttpStatus.FORBIDDEN, ErrorMessage.USER_NOT_COMPLETED_SIGN_UP.getMessage());
    }
}
