package com.lokoko.domain.user.exception;

import com.lokoko.global.auth.exception.ErrorMessage;
import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidRoleTransitionException extends BaseException {
    public InvalidRoleTransitionException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.ROLE_TRANSITION_NOT_ALLOWED.getMessage());
        ;
    }
}
