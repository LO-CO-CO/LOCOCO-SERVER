package com.lokoko.domain.creator.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CreatorInstaLinkInvalidException extends BaseException {
    public CreatorInstaLinkInvalidException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_INSTA_LINK.getMessage());
    }
}