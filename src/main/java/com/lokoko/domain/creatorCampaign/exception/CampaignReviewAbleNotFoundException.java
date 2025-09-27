package com.lokoko.domain.creatorCampaign.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CampaignReviewAbleNotFoundException extends BaseException {
    public CampaignReviewAbleNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.CREATOR_CAMPAIGN_REVIEW_ABLE_NOT_FOUND.getMessage());
    }
}
