package com.lokoko.domain.creator.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NotCreatorRoleException extends BaseException {
    public NotCreatorRoleException() {
        super(HttpStatus.FORBIDDEN, ErrorMessage.ONLY_CREATOR_ROLE_SIGN_UP.getMessage());
    }
}