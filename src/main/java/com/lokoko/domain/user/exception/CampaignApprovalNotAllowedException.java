package com.lokoko.domain.user.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CampaignApprovalNotAllowedException extends BaseException {
    public CampaignApprovalNotAllowedException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.ADMIN_CAMPAIGN_APPROVAL_NOT_ALLOWED.getMessage());
    }
}
