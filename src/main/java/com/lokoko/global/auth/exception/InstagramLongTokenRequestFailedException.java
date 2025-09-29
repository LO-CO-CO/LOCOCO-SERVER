package com.lokoko.global.auth.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InstagramLongTokenRequestFailedException extends BaseException {
    public InstagramLongTokenRequestFailedException() {
        super(HttpStatus.BAD_GATEWAY, ErrorMessage.INSTAGRAM_LONG_TOKEN_REQUEST_FAILED.getMessage());
    }
}
