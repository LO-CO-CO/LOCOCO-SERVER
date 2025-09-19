package com.lokoko.domain.campaign.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NoApplicableCreatorsException extends BaseException {
    public NoApplicableCreatorsException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.NO_APPLICABLE_CREATOR.getMessage());
    }
}
