package com.lokoko.domain.creatorCampaign.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AlreadyParticipatedCampaignException extends BaseException {
    public AlreadyParticipatedCampaignException() {
        super(HttpStatus.CONFLICT, ErrorMessage.ALREADY_PARTICIPATED_CAMPAIGN.getMessage());
    }
}
