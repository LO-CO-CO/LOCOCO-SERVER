package com.lokoko.domain.creatorCampaign.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CampaignNotRecruitingException extends BaseException {
    public CampaignNotRecruitingException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.CAMPAIGN_NOT_RECRUITING.getMessage());
    }
}
