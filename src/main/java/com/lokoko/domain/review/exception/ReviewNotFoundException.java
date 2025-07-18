package com.lokoko.domain.review.exception;

import static com.lokoko.domain.review.exception.ErrorMessage.REVIEW_NOT_FOUND;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReviewNotFoundException extends BaseException {

    public ReviewNotFoundException() {
        super(HttpStatus.NOT_FOUND, REVIEW_NOT_FOUND.getMessage());
    }
}
