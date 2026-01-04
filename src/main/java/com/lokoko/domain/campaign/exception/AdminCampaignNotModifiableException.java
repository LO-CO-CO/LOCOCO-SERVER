package com.lokoko.domain.campaign.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AdminCampaignNotModifiableException extends BaseException {
    public AdminCampaignNotModifiableException(){
        super(HttpStatus.BAD_REQUEST, ErrorMessage.ADMIN_CAMPAIGN_NOT_MODIFIABLE.getMessage());
    }
}
