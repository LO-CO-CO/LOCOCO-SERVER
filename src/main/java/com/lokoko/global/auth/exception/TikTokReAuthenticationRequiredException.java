package com.lokoko.global.auth.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TikTokReAuthenticationRequiredException extends BaseException {
    public TikTokReAuthenticationRequiredException() {
        super(HttpStatus.BAD_GATEWAY, ErrorMessage.TIKTOK_PROFILE_FETCH_FAILED.getMessage());
    }
    
}