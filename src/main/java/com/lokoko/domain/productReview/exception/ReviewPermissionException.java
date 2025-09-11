package com.lokoko.domain.productReview.exception;

import static com.lokoko.domain.productReview.exception.ErrorMessage.REVIEW_DELETE_FORBIDDEN;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReviewPermissionException extends BaseException {
    public ReviewPermissionException() {
        super(HttpStatus.FORBIDDEN, REVIEW_DELETE_FORBIDDEN.getMessage());
    }
}
