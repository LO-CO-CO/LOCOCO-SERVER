package com.lokoko.domain.campaign.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CampaignCapacityExceedException extends BaseException {
    public CampaignCapacityExceedException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.CAMPAIGN_CAPACITY_EXCEED.getMessage());
    }
}
