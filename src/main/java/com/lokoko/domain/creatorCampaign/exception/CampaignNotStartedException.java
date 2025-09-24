package com.lokoko.domain.creatorCampaign.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CampaignNotStartedException extends BaseException {
    public CampaignNotStartedException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.CAMPAIGN_NOT_STARTED.getMessage());
    }
}
