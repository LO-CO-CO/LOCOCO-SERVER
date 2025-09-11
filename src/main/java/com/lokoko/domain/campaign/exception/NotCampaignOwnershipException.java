package com.lokoko.domain.campaign.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NotCampaignOwnershipException extends BaseException {
    public NotCampaignOwnershipException() {
        super(HttpStatus.FORBIDDEN, ErrorMessage.NOT_CAMPAIGN_OWNER.getMessage());
    }
}
