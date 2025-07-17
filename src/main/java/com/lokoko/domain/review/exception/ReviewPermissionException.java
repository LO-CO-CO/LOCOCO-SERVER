package com.lokoko.domain.review.exception;

import static com.lokoko.domain.review.exception.ErrorMessage.REVIEW_DELETE_FORBIDDEN;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReviewPermissionException extends BaseException {
    public ReviewPermissionException() {
        super(HttpStatus.FORBIDDEN, REVIEW_DELETE_FORBIDDEN.getMessage());
    }
}
