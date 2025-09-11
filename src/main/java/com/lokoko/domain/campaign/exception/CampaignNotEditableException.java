package com.lokoko.domain.campaign.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CampaignNotEditableException extends BaseException {

    public CampaignNotEditableException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.NOT_EDITABLE_CAMPAIGN.getMessage());
    }
}