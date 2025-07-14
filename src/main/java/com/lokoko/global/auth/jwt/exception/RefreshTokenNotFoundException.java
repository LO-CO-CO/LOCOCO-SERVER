package com.lokoko.global.auth.jwt.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RefreshTokenNotFoundException extends BaseException {
    public RefreshTokenNotFoundException() {
        super(HttpStatus.UNAUTHORIZED, JwtErrorMessage.REFRESH_NOT_FOUND.getMessage());
    }
}
