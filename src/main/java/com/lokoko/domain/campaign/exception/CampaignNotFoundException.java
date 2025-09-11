package com.lokoko.domain.campaign.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CampaignNotFoundException extends BaseException {
    public CampaignNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.CAMPAIGN_NOT_FOUND.getMessage());
    }
}
