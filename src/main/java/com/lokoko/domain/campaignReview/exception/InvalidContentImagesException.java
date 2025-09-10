package com.lokoko.domain.campaignReview.exception;

import static com.lokoko.domain.campaignReview.exception.ErrorMessage.INVALID_CONTENT_IMAGES;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidContentImagesException extends BaseException {
    public InvalidContentImagesException() {
        super(HttpStatus.BAD_REQUEST, INVALID_CONTENT_IMAGES.getMessage());
    }
}
