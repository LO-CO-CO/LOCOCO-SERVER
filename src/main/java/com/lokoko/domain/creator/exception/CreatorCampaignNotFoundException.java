package com.lokoko.domain.creator.exception;

import static com.lokoko.domain.creator.exception.ErrorMessage.CREATOR_CAMPAIGN_NOT_FOUND;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CreatorCampaignNotFoundException extends BaseException {
    public CreatorCampaignNotFoundException() {
        super(HttpStatus.NOT_FOUND, CREATOR_CAMPAIGN_NOT_FOUND.getMessage());
    }
}
