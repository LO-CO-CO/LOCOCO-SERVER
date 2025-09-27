package com.lokoko.domain.campaignReview.application.service;

import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.campaign.exception.CampaignNotViewableException;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import org.springframework.stereotype.Component;

@Component
public class CampaignReviewStatusManager {

    /**
     * 브랜드 뷰에서 어떤 리뷰 라운드를 보여줄지 결정해주는 상태관리 메서드입니다 (총 4가지 case)
     * <p>
     * 캠페인 상태가 IN_REVIEW 인 경우 case)
     * <li>1) APPROVED_FIRST_REVIEW_DONE, APPROVED_REVISION_REQUESTED, APPROVED_REVISION_CONFIRMED → FIRST</li>
     * <li>2) APPROVED_SECOND_REVIEW_DONE → SECOND</li>
     *
     * <li>3) 캠페인 상태가 COMPLETED + 참여 상태가 APPROVED_SECOND_REVIEW_DONE case) SECOND</li>
     * <li>4) 그 외 case) 브랜드가 볼 수 없음 → CampaignNotViewableException</li>
     * </ul>
     */
    public ReviewRound determineReviewRound(CampaignStatus campaignStatus,
                                            ParticipationStatus participationStatus) {

        if (campaignStatus == CampaignStatus.IN_REVIEW) {
            return switch (participationStatus) {
                case APPROVED_FIRST_REVIEW_DONE,
                     APPROVED_REVISION_REQUESTED,
                     APPROVED_REVISION_CONFIRMED -> ReviewRound.FIRST;
                case APPROVED_SECOND_REVIEW_DONE -> ReviewRound.SECOND;
                default -> throw new CampaignNotViewableException();
            };
        }
        if (campaignStatus == CampaignStatus.COMPLETED
                && participationStatus == ParticipationStatus.APPROVED_SECOND_REVIEW_DONE) {
            return ReviewRound.SECOND;
        }
        throw new CampaignNotViewableException();
    }

    /**
     * 크리에이터 관점에서 리뷰 라운드 상태 관련 메서드
     *
     * <p>두 가지 관점에서 리뷰 라운드를 판별합니다:
     * <ul>
     *   <li>브랜드 관점: 캠페인 상태(CampaignStatus)와 참여 상태(ParticipationStatus)를 함께 보고
     *       브랜드가 현재 어떤 리뷰 라운드를 확인할 수 있는지 결정
     *       (예: IN_REVIEW + APPROVED_FIRST_REVIEW_DONE → FIRST)</li>
     *   <li>크리에이터 관점: 참여 상태(ParticipationStatus)만으로 현재 업로드 가능한 리뷰 라운드를 매핑
     * </ul>
     */
    public ReviewRound mapRoundForCreator(ParticipationStatus participationStatus) {
        return switch (participationStatus) {
            case APPROVED_ADDRESS_CONFIRMED -> ReviewRound.FIRST;
            case APPROVED_FIRST_REVIEW_DONE,
                 APPROVED_REVISION_REQUESTED,
                 APPROVED_REVISION_CONFIRMED -> ReviewRound.SECOND;
            default -> throw new CampaignNotViewableException();
        };
    }
}
