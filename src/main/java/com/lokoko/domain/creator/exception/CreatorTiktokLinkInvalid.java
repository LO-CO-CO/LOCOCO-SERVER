package com.lokoko.domain.creator.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;


public class CreatorTiktokLinkInvalid extends BaseException {
    public CreatorTiktokLinkInvalid() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_TIKTOK_LINK.getMessage());
    }
}