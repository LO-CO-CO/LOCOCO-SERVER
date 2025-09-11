package com.lokoko.domain.creator.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CreatorInfoNotCompletedException extends BaseException {
    public CreatorInfoNotCompletedException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.CREATOR_BASIC_INFO_NOT_COMPLETED.getMessage());
    }
}