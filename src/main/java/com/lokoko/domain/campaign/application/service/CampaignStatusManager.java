package com.lokoko.domain.campaign.application.service;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.campaign.domain.entity.enums.ParticipationStatus;
import com.lokoko.domain.campaign.exception.CampaignNotAccessibleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
/**
 * 캠페인 관련 상태들을 관리하는 총괄 클래스
 */
public class CampaignStatusManager {

    /**
     * 브랜드 관점에서 보는 Campaign 의 상태(피그마 뷰 - [브랜드] 마이페이지의 상태표 참고)
     * 날짜 기반으로 실제 상태를 계산
     * @param campaign
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
     * DB 업데이트 없이 실시간 상태만 계산
     * 비즈니스 로직에서 상태 검증이 필요할 때 사용
     * @param campaign
     * @return 현재 시점의 실제 캠페인 상태
     */
    public CampaignStatus calculateRealTimeStatus(Campaign campaign) {
        // determineCampaignStatus와 동일한 로직이지만 DB 업데이트 없음
        return determineCampaignStatus(campaign);
    }

    /**
     * @param campaign 상태를 동기화할 캠페인
     * @return 업데이트된 상태
     */
    @Transactional
    public CampaignStatus syncCampaignStatus(Campaign campaign) {
        CampaignStatus calculatedStatus = determineCampaignStatus(campaign);

        if (campaign.getCampaignStatus() != calculatedStatus) {
            campaign.changeStatus(calculatedStatus);
        }
        return calculatedStatus;
    }

    /**
     * 캠페인 상세조회 페이지에서, 캠페인의 상태 판단
     * db 와 상태 대조후, 동기화.
     */
    @Transactional
    public String determineStatusInDetailPage(Campaign campaign, Optional<CreatorCampaign> creatorCampaign) {

        // 상태 동기화 먼저
        CampaignStatus campaignStatus = syncCampaignStatus(campaign);

        if (campaignStatus == CampaignStatus.DRAFT || campaignStatus == CampaignStatus.WAITING_APPROVAL) {
            throw new CampaignNotAccessibleException(); // DRAFT 상태와, WAITING_APPROVAL 상태인 캠페인은 보여지면 안 된다.
        }

        String returnStatus = null;

        // 크리에이터의 지원 없는 상태
        if (creatorCampaign.isEmpty()) {
            if (campaignStatus == CampaignStatus.OPEN_RESERVED){ // 캠페인 승인 받고, 오픈 예정인 상태
                returnStatus = "OPEN_RESERVED";
            }
            if (campaignStatus == CampaignStatus.RECRUITING){  // 캠페인 모집 중인데, 크리에이터 지원 아직 안한 경우
                returnStatus = "RECRUITING";
            }
            if (campaignStatus == CampaignStatus.RECRUITMENT_CLOSED
                    || campaignStatus == CampaignStatus.IN_REVIEW
                    || campaignStatus == CampaignStatus.COMPLETED) {
                returnStatus = "NOT_APPLIED_ENDED";
            }
        } else { // 크리에이터 지원 있는 상태
            CreatorCampaign participation = creatorCampaign.get(); // 크리에이터의 참여 정보
            ParticipationStatus participationStatus = participation.getStatus(); // 참여 상태

            // 결과 나오기 전
            // 대기 상태
            if (participationStatus == ParticipationStatus.PENDING) {
                returnStatus = "PENDING";
            }
            // 거절됨
            else if (participationStatus == ParticipationStatus.REJECTED) {
                returnStatus = "REJECTED";
            }
            // 승인됨 - 초기 상태
            else if (participationStatus == ParticipationStatus.APPROVED) {
                returnStatus = "APPROVED_PENDING_ACTION";
            }
            // 승인됨 - 진행 상태들
            else if (participationStatus == ParticipationStatus.APPROVED_ADDRESS_CONFIRMED) {
                returnStatus = "APPROVED_ADDRESS_CONFIRMED";
            }
            else if (participationStatus == ParticipationStatus.APPROVED_FIRST_REVIEW_DONE) {
                returnStatus = "APPROVED_FIRST_REVIEW_DONE";
            }
            else if (participationStatus == ParticipationStatus.APPROVED_REVISION_REQUESTED) {
                returnStatus = "APPROVED_REVISION_REQUESTED";
            }
            else if (participationStatus == ParticipationStatus.APPROVED_REVISION_CONFIRMED) {
                returnStatus = "APPROVED_REVISION_CONFIRMED";
            }
            // 완료 상태
            else if (participationStatus == ParticipationStatus.APPROVED_SECOND_REVIEW_DONE) {
                returnStatus = "APPROVED_SECOND_REVIEW_DONE";
            }
            // 만료 상태들
            else if (participationStatus == ParticipationStatus.APPROVED_ADDRESS_NOT_CONFIRMED) {
                returnStatus = "APPROVED_ADDRESS_NOT_CONFIRMED";
            }
            else if (participationStatus == ParticipationStatus.APPROVED_REVIEW_NOT_CONFIRMED) {
                returnStatus = "APPROVED_REVIEW_NOT_CONFIRMED";
            }
        }

        if (returnStatus == null) {
            returnStatus = "UNKNOWN";
        }

        return returnStatus;
    }
}
