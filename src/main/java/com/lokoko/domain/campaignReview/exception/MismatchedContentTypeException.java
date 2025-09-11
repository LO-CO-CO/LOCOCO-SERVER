package com.lokoko.domain.campaignReview.exception;

import static com.lokoko.domain.campaignReview.exception.ErrorMessage.MISMATCHED_CONTENT_TYPE;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MismatchedContentTypeException extends BaseException {
    public MismatchedContentTypeException() {
        super(HttpStatus.BAD_REQUEST, MISMATCHED_CONTENT_TYPE.getMessage());
    }
}
