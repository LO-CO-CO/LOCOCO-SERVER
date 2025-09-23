package com.lokoko.domain.campaign.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CampaignApplicantBulkUpdateException extends BaseException {
    public CampaignApplicantBulkUpdateException() {
        super(HttpStatus.CONFLICT, ErrorMessage.CAMPAIGN_APPLICANT_BULK_UPDATE_FAILED.getMessage());
    }
}
