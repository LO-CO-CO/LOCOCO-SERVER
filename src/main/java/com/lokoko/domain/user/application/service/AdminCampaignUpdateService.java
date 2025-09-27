package com.lokoko.domain.user.application.service;

import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.user.exception.CampaignApprovalNotAllowedException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCampaignUpdateService {

    private final CampaignGetService campaignGetService;

    @Transactional
    public void approve(Long campaignId, Instant now) {
        Campaign campaign = campaignGetService.findByCampaignId(campaignId);

        if (campaign.getCampaignStatus() != CampaignStatus.WAITING_APPROVAL) {
            throw new CampaignApprovalNotAllowedException();
        }

        CampaignStatus next = CampaignStatus.RECRUITING;
        Instant applyStart = campaign.getApplyStartDate();
        if (applyStart != null && now.isBefore(applyStart)) {
            next = CampaignStatus.OPEN_RESERVED;
        }

        campaign.changeStatus(next);
    }
}
