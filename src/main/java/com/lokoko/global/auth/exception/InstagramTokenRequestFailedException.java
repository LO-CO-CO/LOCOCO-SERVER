package com.lokoko.global.auth.exception;


import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InstagramTokenRequestFailedException extends BaseException {
    public InstagramTokenRequestFailedException() {
        super(HttpStatus.BAD_GATEWAY, ErrorMessage.INSTAGRAM_TOKEN_REQUEST_FAILED.getMessage());
    }
}
