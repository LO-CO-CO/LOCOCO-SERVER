package com.lokoko.domain.user.application.service;

import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.campaign.domain.repository.CampaignRepository;
import com.lokoko.domain.campaign.exception.AdminCampaignNotModifiableException;
import com.lokoko.domain.scheduler.application.service.CampaignEventScheduler;
import com.lokoko.domain.user.api.dto.request.CampaignModifyRequest;
import com.lokoko.domain.user.exception.CampaignApprovalNotAllowedException;
import java.time.Instant;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCampaignUpdateService {

    private final CampaignGetService campaignGetService;
    private final CampaignEventScheduler campaignEventScheduler;

    private final CampaignRepository campaignRepository;

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

        // 관리자 승인 후 스케줄 이벤트 등록
        campaignEventScheduler.scheduleCampaignEvents(campaign);
    }

    @Transactional
    public void approveCampaigns(List<Long> campaignIds) {
        Instant now = Instant.now();

        List<Campaign> campaigns = campaignRepository.findAllById(campaignIds);

        for (Campaign campaign : campaigns) {
            if (campaign.getCampaignStatus() != CampaignStatus.WAITING_APPROVAL) {
                continue;
            }
            CampaignStatus next = CampaignStatus.RECRUITING;
            Instant applyStart = campaign.getApplyStartDate();
            if (applyStart != null && now.isBefore(applyStart)) {
                next = CampaignStatus.OPEN_RESERVED;
            }
            campaign.changeStatus(next);
        }
        campaignEventScheduler.scheduleCampaignEvents(campaignIds);
    }

    @Transactional
    public void modifyCampaign(Long campaignId, CampaignModifyRequest modifyRequest){
        Campaign campaign = campaignGetService.findByCampaignId(campaignId);

        // 캠페인이 WAITING_APPROVAL 외의 상태이면 예외 반환
        if (campaign.getCampaignStatus() != CampaignStatus.WAITING_APPROVAL){
            throw new AdminCampaignNotModifiableException();
        }
        campaign.updateCampaign(modifyRequest);
    }
}
