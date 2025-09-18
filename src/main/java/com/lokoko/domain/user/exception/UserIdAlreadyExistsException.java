package com.lokoko.domain.user.exception;

import com.lokoko.global.auth.exception.ErrorMessage;
import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserIdAlreadyExistsException extends BaseException {
    public UserIdAlreadyExistsException() {
        super(HttpStatus.CONFLICT, ErrorMessage.USER_ID_ALREADY_EXIST.getMessage());
    }
}
