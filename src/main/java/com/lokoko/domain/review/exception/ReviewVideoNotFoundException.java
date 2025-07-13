package com.lokoko.domain.review.exception;

import static com.lokoko.domain.review.exception.ErrorMessage.REVIEW_VIDEO_NOT_FOUND;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReviewVideoNotFoundException extends BaseException {

    public ReviewVideoNotFoundException() {
        super(HttpStatus.NOT_FOUND, REVIEW_VIDEO_NOT_FOUND.getMessage());
    }
}
