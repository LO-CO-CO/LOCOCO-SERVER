package com.lokoko.global.auth.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InstagramReAuthenticationRequiredException extends BaseException {
    public InstagramReAuthenticationRequiredException() {
        super(HttpStatus.UNAUTHORIZED, ErrorMessage.INSTAGRAM_RECONNECTION_REQUIRED.getMessage());
    }
}
