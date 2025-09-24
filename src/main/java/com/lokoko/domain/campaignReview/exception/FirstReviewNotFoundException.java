package com.lokoko.domain.campaignReview.exception;

import static com.lokoko.domain.campaignReview.exception.ErrorMessage.FIRST_REVIEW_NOT_FOUND;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class FirstReviewNotFoundException extends BaseException {
    public FirstReviewNotFoundException() {
        super(HttpStatus.NOT_FOUND, FIRST_REVIEW_NOT_FOUND.getMessage());
    }
}
