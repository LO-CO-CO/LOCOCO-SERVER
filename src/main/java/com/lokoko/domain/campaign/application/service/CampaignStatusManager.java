package com.lokoko.domain.campaign.application.service;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.campaign.domain.entity.enums.ParticipationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
/**
 * 캠페인 관련 상태들을 관리하는 총괄 클래스
 */
public class CampaignStatusManager {

    /**
     * 브랜드 관점에서 보는 Campagin 의 상태(피그마 뷰 - [브랜드] 마이페이지의 상태표 참고)
     * @param campaign
     * @return 브랜드 마이페이지에서 확인할 수 있는 캠페인의 상태
     */
    public CampaignStatus determineCampaignStatus(Campaign campaign) {

        if (campaign.isDraft()) {
            return CampaignStatus.DRAFT;
        }

        Instant now = Instant.now();
        // 승인 대기인 상태(관리자의 승인이 필요한 경우)
        if (campaign.getCampaignStatus() == CampaignStatus.WAITING_APPROVAL) {
            return CampaignStatus.WAITING_APPROVAL;
        }

        // 관리자 승인 받고 나서, 캠페인 시작 전인 상태
        if (now.isBefore(campaign.getApplyStartDate())) {
            return CampaignStatus.OPEN_RESERVED;
        }

        // 캠페인 시작 시간부터, 크리에이터 모집 마감 전까지의 상태
        if (now.isBefore(campaign.getApplyDeadline())) {
            return CampaignStatus.RECRUITING;
        }

        // 크리에이터 모집 마감 시간부터, 크리에이터 결과 발표 전까지의 상태
        if (now.isBefore(campaign.getCreatorAnnouncementDate())) {
            return CampaignStatus.RECRUITMENT_CLOSED;
        }

        // 크리에이터 결과 발표 시간부터, 리뷰 제출 마감 전까지의 상태
        if (now.isBefore(campaign.getReviewSubmissionDeadline())) {
            return CampaignStatus.IN_REVIEW;
        }

        // 리뷰 제출 마감시간부터 ~ 그 이후
        // 캠페인 종료
        return CampaignStatus.COMPLETED;
    }
}
