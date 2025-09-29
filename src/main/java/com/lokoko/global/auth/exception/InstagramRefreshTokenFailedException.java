package com.lokoko.global.auth.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InstagramRefreshTokenFailedException extends BaseException {
    public InstagramRefreshTokenFailedException() {
        super(HttpStatus.BAD_GATEWAY, ErrorMessage.INSTAGRAM_REFRESH_TOKEN_FAILED.getMessage());
    }
}
