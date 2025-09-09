package com.lokoko.domain.productReview.exception;

import static com.lokoko.global.auth.exception.ErrorMessage.ROLE_TRANSITION_NOT_ALLOWED;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReviewAlreadySubmittedException extends BaseException {
    public ReviewAlreadySubmittedException() {
        super(HttpStatus.CONFLICT, ROLE_TRANSITION_NOT_ALLOWED.getMessage());
    }
}
