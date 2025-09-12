package com.lokoko.domain.creator.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;


public class CreatorNotFoundException extends BaseException {
    public CreatorNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.CREATOR_NOT_FOUND.getMessage());
    }
}