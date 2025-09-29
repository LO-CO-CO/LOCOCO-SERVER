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
                default -> null;
            };
        } else { // 크리에이터 지원 있는 상태
            ParticipationStatus participationStatus = creatorCampaign.get().getStatus();

            // 우선순위 1: 만료/거절 상태는 캠페인 상태와 관계없이 우선 표시
            if (participationStatus == ParticipationStatus.REJECTED) {
                return CampaignDetailPageStatus.REJECTED;
            }
            if (participationStatus == ParticipationStatus.EXPIRED) {
                return CampaignDetailPageStatus.APPROVED_ADDRESS_NOT_CONFIRMED; // 기존 호환성 유지
            }

            // 우선순위 2: 완료 상태
            if (participationStatus == ParticipationStatus.COMPLETED) {
                return CampaignDetailPageStatus.APPROVED_SECOND_REVIEW_DONE; // 기존 호환성 유지
            }

            // 우선순위 3: 캠페인 전체 상태 고려
            return switch (campaignStatus) {
                case COMPLETED -> CampaignDetailPageStatus.CLOSED;
                case IN_REVIEW, RECRUITMENT_CLOSED -> CampaignDetailPageStatus.ACTIVE;
                default -> CampaignDetailPageStatus.APPLIED; // RECRUITING 및 기타 상태
            };
        }
    }

    /**
     * 비로그인 사용자와 Customer 를 고려한 캠페인 상세 페이지 상태 결정
     *
     * @param campaign 캠페인 엔티티
     * @return 캠페인 상세페이지 상태
     */
    public CampaignDetailPageStatus determineStatusForNonLoggedInAndCustomer(Campaign campaign) {
        CampaignStatus campaignStatus = determineCampaignStatus(campaign);

        if (campaignStatus == CampaignStatus.DRAFT || campaignStatus == CampaignStatus.WAITING_APPROVAL) {
            throw new CampaignNotViewableException();
        }

        return switch (campaignStatus) {
            case OPEN_RESERVED -> CampaignDetailPageStatus.OPEN_RESERVED;
            case RECRUITING -> CampaignDetailPageStatus.RECRUITING;
            case RECRUITMENT_CLOSED, IN_REVIEW -> CampaignDetailPageStatus.ACTIVE;
            case COMPLETED -> CampaignDetailPageStatus.CLOSED;
            default -> null;
        };
    }

    /**
     * 브랜드 사용자를 위한 캠페인 상세 페이지 상태 결정
     *
     * @param campaign 캠페인 엔티티
     * @return 캠페인 상세페이지 상태
     */
    public CampaignDetailPageStatus determineStatusForBrandAndAdmin(Campaign campaign) {

        CampaignStatus campaignStatus = determineCampaignStatus(campaign);
        Instant now = Instant.now();

        if (campaignStatus == CampaignStatus.DRAFT || campaignStatus == CampaignStatus.WAITING_APPROVAL) {
            throw new CampaignNotViewableException();
        }

        if (campaignStatus == CampaignStatus.OPEN_RESERVED) {
            return CampaignDetailPageStatus.OPEN_RESERVED;
        }

        if (now.isAfter(campaign.getApplyStartDate()) && now.isBefore(campaign.getReviewSubmissionDeadline())) {
            return CampaignDetailPageStatus.ACTIVE;
        }

        if (campaignStatus == CampaignStatus.COMPLETED) {
            return CampaignDetailPageStatus.CLOSED;
        }

        return null;
    }
}
