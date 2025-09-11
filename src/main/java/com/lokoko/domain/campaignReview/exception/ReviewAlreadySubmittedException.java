package com.lokoko.domain.campaignReview.exception;

import static com.lokoko.domain.campaignReview.exception.ErrorMessage.REVIEW_ALREADY_SUBMITTED;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReviewAlreadySubmittedException extends BaseException {
    public ReviewAlreadySubmittedException() {
        super(HttpStatus.CONFLICT, REVIEW_ALREADY_SUBMITTED.getMessage());
    }
}
