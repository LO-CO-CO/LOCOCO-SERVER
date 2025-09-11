package com.lokoko.domain.campaignReview.exception;

import static com.lokoko.domain.campaignReview.exception.ErrorMessage.BRAND_NOTE_REQUIRED_FOR_SECOND;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BrandNoteRequiredForSecondException extends BaseException {
    public BrandNoteRequiredForSecondException() {
        super(HttpStatus.BAD_REQUEST, BRAND_NOTE_REQUIRED_FOR_SECOND.getMessage());
    }
}
