package com.lokoko.domain.campaign.exception;

import static com.lokoko.domain.campaign.exception.ErrorMessage.CAMPAIGN_EXPIRED;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CampaignExpiredException extends BaseException {
    public CampaignExpiredException() {
        super(HttpStatus.BAD_REQUEST, CAMPAIGN_EXPIRED.getMessage());
    }
}
