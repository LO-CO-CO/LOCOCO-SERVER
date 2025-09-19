package com.lokoko.domain.campaignReview.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CampaignReviewNotFoundException extends BaseException {
    public CampaignReviewNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.CAMPAIGN_REVIEW_NOT_FOUND.getMessage());
    }
}
