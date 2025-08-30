package com.lokoko.domain.campagin.application.service;

import com.lokoko.domain.campagin.domain.entity.Campaign;
import com.lokoko.domain.campagin.domain.entity.CreatorCampaign;
import com.lokoko.domain.campagin.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.campagin.domain.entity.enums.ParticipationStatus;
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
     * 크리에이터 관점에서 보는 캠페인의 상태(피그마 뷰 - 크리에이터 체험단 신청의 상태표 참고)
     * @return 캠페인 상세 조회에서 확인할 수 있는 캠페인 상태메시지
     */
    public String getCampaignStatusMessage(Campaign campaign, CreatorCampaign participation) {
        Instant now = Instant.now();

        // 캠페인 오픈 전
        if (now.isBefore(campaign.getApplyStartDate())) {
            return "Coming Soon";
        }

        // 모집 중
        if (determineCampaignStatus(campaign) == CampaignStatus.RECRUITING) {
            if (participation != null) { // 지원하지 않았다면
                return "Apply Now!";
            } else { // 이미 지원한 상태라면
                return "Successfully Applied";
            }
        }

        // 여기서부터 캠페인 모집 종료 상태
        // 모집 종료 후 (지원한 캠페인이라면)
        if (participation != null) {
            ParticipationStatus participationStatus = participation.getStatus();
            switch (participationStatus){
                case PENDING:
                    return "Successfully Applied"; // 결과 대기중
                case REJECTED:
                    return "Campaign not Selected"; // 거절
                case ACTIVE: // 현재 피그마 뷰 상으로 명시적인 메시지가 없으므로 보류
                case COMPLETED:
                    return "Campaign Completed";
                case EXPIRED:
                    return "Campaign Expired";
            }

        }
        // 모집 종료 후 (지원하지 않은 캠페인이라면)
        return "Campaign Closed";
    }

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
        if (campaign.getCampaignStatus() == CampaignStatus.WAITING_APPROVE) {
            return CampaignStatus.WAITING_APPROVE;
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
     * 크리에이터가 참여한 캠페인의 상태 결정
     * 피그마 뷰 - 크리에이터 마이페이지의 상태표 참고
     */
    public void updateParticipationStatus(CreatorCampaign creatorCampaign) {
        ParticipationStatus currentStatus = creatorCampaign.getStatus();
        Campaign campaign = creatorCampaign.getCampaign();
        Instant now = Instant.now();

        switch (currentStatus) {
            case PENDING:
                // 크리에이터 선정 기간이 지난 경우 PENDING -> REJECTED 처리
                if (now.isAfter(campaign.getCreatorAnnouncementDate())) {
                    creatorCampaign.changeStatus(ParticipationStatus.REJECTED);
                }
                break;
            case APPROVED:
                // 배송지 입력을 했다면, ACTIVE 로 전환
                if (creatorCampaign.isAddressConfirmed()) {
                    creatorCampaign.changeStatus(ParticipationStatus.ACTIVE);
                }
                // 배송지 확인 기간 초과되면, EXPIRED (배송지 확인 기간은 7일로 임시 선정)
                else if (now.isAfter(campaign.getCreatorAnnouncementDate().plus(7, ChronoUnit.DAYS))) {
                    creatorCampaign.changeStatus(ParticipationStatus.EXPIRED);
                }
                break;
                
            case ACTIVE:
                // 2차 리뷰 까지 완료했다면, COMPLETED 로 전환
                if (creatorCampaign.isSecondReviewSubmitted()) {
                    creatorCampaign.changeStatus(ParticipationStatus.COMPLETED);
                }
                // 리뷰 제출 기한 초과 시
                else if (now.isAfter(campaign.getReviewSubmissionDeadline())) {
                    creatorCampaign.changeStatus(ParticipationStatus.EXPIRED);
                }
                break;
                
            default:
                // 예상치 못한 상태는 그대로 유지
                break;
        }
    }

    /**
     * 캠페인 선정(당첨)시 상태 변경
     */
    public void approveCreator(CreatorCampaign participation) {
        participation.changeStatus(ParticipationStatus.APPROVED);
    }

    /**
     * 캠페인 탈락시 상태 변경
     */
    public void rejectCreator(CreatorCampaign participation) {
        participation.changeStatus(ParticipationStatus.REJECTED);
    }

    /**
     * 배송지 확인시
     */
    public void confirmAddress(CreatorCampaign participation) {
        participation.changeAddressConfirmed(true);
    }

    /**
     * 1차 리뷰 제출
     */
    public void submitFirstReview(CreatorCampaign participation) {
        participation.changeFirstReviewSubmitted(true);
    }

    /**
     * 브랜드 수정 요청
     */
    public void requestRevision(CreatorCampaign participation) {
        participation.changeRevisionRequested(true);
    }

    /**
     * 2차 리뷰 제출
     */
    public void submitSecondReview(CreatorCampaign participation) {
        participation.changeSecondReviewSubmitted(true);
        updateParticipationStatus(participation); // COMPLETED로 자동 전환
    }




}
