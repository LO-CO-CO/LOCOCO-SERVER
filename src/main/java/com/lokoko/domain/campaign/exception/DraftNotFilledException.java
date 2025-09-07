package com.lokoko.domain.campaign.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DraftNotFilledException extends BaseException {
    public DraftNotFilledException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.DRAFT_NOT_FILLED.getMessage());
    }
}
