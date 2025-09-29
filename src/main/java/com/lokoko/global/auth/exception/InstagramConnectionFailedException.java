package com.lokoko.global.auth.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InstagramConnectionFailedException extends BaseException {
    public InstagramConnectionFailedException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.INSTAGRAM_CONNECTION_FAILED.getMessage());
    }
}
