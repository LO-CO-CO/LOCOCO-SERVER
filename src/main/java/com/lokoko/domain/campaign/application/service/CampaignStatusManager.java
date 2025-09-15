package com.lokoko.domain.campaign.application.service;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignDetailPageStatus;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.campaign.exception.CampaignNotViewableException;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
/**
 * 캠페인 관련 상태들을 관리하는 총괄 클래스
 */
public class CampaignStatusManager {

    /**
     * 브랜드 관점에서 보는 Campaign 의 상태(피그마 뷰 - [브랜드] 마이페이지의 상태표 참고) <br> 날짜 기반으로 캠페인의 실시간  상태를 계산한다.
     *
     * @param campaign 캠페인 엔티티
     * @return 캠페인 자체 상태
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

    /**
     * 캠페인 상세조회 페이지에서, 캠페인의 상태 판단 <br> 캠페인의 상태를 실시간으로 계산합니다.
     *
     * @param campaign        캠페인 엔티티
     * @param creatorCampaign 크리에이터의 캠페인 참여정보
     * @return 캠페인 상세페이지에서 캠페인 및 크리에이터 지원 관련 상태
     */
    public CampaignDetailPageStatus determineStatusInDetailPage(Campaign campaign,
                                                                Optional<CreatorCampaign> creatorCampaign) {

        // 실시간 상태 계산
        CampaignStatus campaignStatus = determineCampaignStatus(campaign);

        if (campaignStatus == CampaignStatus.DRAFT || campaignStatus == CampaignStatus.WAITING_APPROVAL) {
            throw new CampaignNotViewableException(); // DRAFT 상태와, WAITING_APPROVAL 상태인 캠페인은 보여지면 안 된다.
        }

        // 크리에이터의 지원 없는 상태
        if (creatorCampaign.isEmpty()) {
            return switch (campaignStatus) {
                case OPEN_RESERVED -> CampaignDetailPageStatus.OPEN_RESERVED;
                case RECRUITING -> CampaignDetailPageStatus.RECRUITING;
                case RECRUITMENT_CLOSED, IN_REVIEW, COMPLETED -> CampaignDetailPageStatus.NOT_APPLIED_ENDED;
                default -> CampaignDetailPageStatus.UNKNOWN;
            };
        } else { // 크리에이터 지원 있는 상태
            ParticipationStatus participationStatus = creatorCampaign.get().getStatus();

            return switch (participationStatus) {
                case PENDING -> CampaignDetailPageStatus.PENDING;
                case REJECTED -> CampaignDetailPageStatus.REJECTED;
                case APPROVED -> CampaignDetailPageStatus.APPROVED_PENDING_ACTION;
                case APPROVED_ADDRESS_CONFIRMED -> CampaignDetailPageStatus.APPROVED_ADDRESS_CONFIRMED;
                case APPROVED_FIRST_REVIEW_DONE -> CampaignDetailPageStatus.APPROVED_FIRST_REVIEW_DONE;
                case APPROVED_REVISION_REQUESTED -> CampaignDetailPageStatus.APPROVED_REVISION_REQUESTED;
                case APPROVED_REVISION_CONFIRMED -> CampaignDetailPageStatus.APPROVED_REVISION_CONFIRMED;
                case APPROVED_SECOND_REVIEW_DONE -> CampaignDetailPageStatus.APPROVED_SECOND_REVIEW_DONE;
                case APPROVED_ADDRESS_NOT_CONFIRMED -> CampaignDetailPageStatus.APPROVED_ADDRESS_NOT_CONFIRMED;
                case APPROVED_REVIEW_NOT_CONFIRMED -> CampaignDetailPageStatus.APPROVED_REVIEW_NOT_CONFIRMED;
            };
        }
    }
}
