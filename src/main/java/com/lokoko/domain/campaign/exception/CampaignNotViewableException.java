package com.lokoko.domain.campaign.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CampaignNotViewableException extends BaseException {
    public CampaignNotViewableException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_CAMPAIGN_STATUS.getMessage());
    }
}
