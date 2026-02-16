package com.lokoko.domain.productReview.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReviewCreatePermissionDeniedException extends BaseException {
    public ReviewCreatePermissionDeniedException() {
        super(HttpStatus.FORBIDDEN, ErrorMessage.REVIEW_CREATE_PERMISSION_DENIED.getMessage());
    }
}
