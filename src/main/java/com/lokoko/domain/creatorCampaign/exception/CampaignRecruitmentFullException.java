package com.lokoko.domain.creatorCampaign.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CampaignRecruitmentFullException extends BaseException {
    public CampaignRecruitmentFullException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.CAMPAIGN_RECRUITMENT_FULL.getMessage());
    }
}
