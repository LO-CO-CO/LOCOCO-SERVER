package com.lokoko.domain.creator.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class SnsNotConnectedException extends BaseException {
    public SnsNotConnectedException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.SNS_NOT_CONNECTED.getMessage());
    }
}