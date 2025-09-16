package com.lokoko.domain.creator.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;


public class CreatorIdAlreadyExistsException extends BaseException {
    public CreatorIdAlreadyExistsException() {
        super(HttpStatus.CONFLICT, ErrorMessage.CREATOR_ID_ALREADY_EXIST.getMessage());
    }
}