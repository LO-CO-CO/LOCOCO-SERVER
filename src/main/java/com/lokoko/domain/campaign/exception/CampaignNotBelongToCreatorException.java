package com.lokoko.domain.campaign.exception;

import static com.lokoko.domain.campaign.exception.ErrorMessage.CAMPAIGN_NOT_BELONG_TO_CREATOR;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CampaignNotBelongToCreatorException extends BaseException {
    public CampaignNotBelongToCreatorException() {
        super(HttpStatus.FORBIDDEN, CAMPAIGN_NOT_BELONG_TO_CREATOR.getMessage());
    }
}
