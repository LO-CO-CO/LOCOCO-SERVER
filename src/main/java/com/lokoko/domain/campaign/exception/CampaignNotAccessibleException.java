package com.lokoko.domain.campaign.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CampaignNotAccessibleException extends BaseException {
    public CampaignNotAccessibleException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_CAMPAIGN_STATUS.getMessage());
    }
}
