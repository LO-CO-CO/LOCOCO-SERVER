package com.lokoko.domain.campaignReview.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidReviewPayloadException extends BaseException {
    public InvalidReviewPayloadException(ErrorMessage em) {
        super(HttpStatus.BAD_REQUEST, em.getMessage());
    }
}