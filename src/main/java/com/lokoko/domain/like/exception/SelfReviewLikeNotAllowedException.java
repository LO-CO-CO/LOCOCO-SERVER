package com.lokoko.domain.like.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class SelfReviewLikeNotAllowedException extends BaseException {
    public SelfReviewLikeNotAllowedException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.SELF_REVIEW_LIKE_NOT_ALLOWED.getMessage());
    }
}
