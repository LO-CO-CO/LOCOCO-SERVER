package com.lokoko.domain.campaignReview.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.lokoko.domain.campaignReview.exception.ErrorMessage.REVISION_REQUEST_NOT_ALLOWED;

public class RevisionRequestNotAllowedException extends BaseException {
    public RevisionRequestNotAllowedException() {
        super(HttpStatus.BAD_REQUEST, REVISION_REQUEST_NOT_ALLOWED.getMessage());
    }
}
