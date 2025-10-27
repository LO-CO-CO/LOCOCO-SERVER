package com.lokoko.domain.campaignReview.application.service;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.creatorCampaign.domain.repository.CreatorCampaignRepository;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.global.config.BetaFeatureConfig;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatorCampaignUpdateService {

    private final CreatorCampaignRepository creatorCampaignRepository;
    private final CampaignReviewGetService campaignReviewGetService;
    private final BetaFeatureConfig betaFeatureConfig;

    @Transactional
    public void refreshParticipationStatus(Long creatorCampaignId) {
        CreatorCampaign creatorCampaign = creatorCampaignRepository.getByIdForUpdate(creatorCampaignId);
        Campaign campaign = creatorCampaign.getCampaign();

        boolean firstExists = campaignReviewGetService.existsFirst(creatorCampaignId);
        boolean secondExists = campaignReviewGetService.existsSecond(creatorCampaignId);

        ParticipationStatus nextStatus;

        if (!Boolean.TRUE.equals(creatorCampaign.getAddressConfirmed())) {
            nextStatus = ParticipationStatus.APPROVED;
        } else if (betaFeatureConfig.isSimplifiedReviewFlow() && firstExists) {
            // 베타 모드: 1차 리뷰 완료시 COMPLETED 상태로 전환
            // 모든 필요한 ContentType에 대해 1차 리뷰가 있는지 확인
            boolean allFirstReviewsComplete = checkAllFirstReviewsComplete(creatorCampaignId, campaign);
            nextStatus = allFirstReviewsComplete ? ParticipationStatus.COMPLETED : ParticipationStatus.ACTIVE;
        } else if (secondExists) {
            // 정식 플로우: 2차 리뷰 존재시 COMPLETED
            nextStatus = ParticipationStatus.COMPLETED;
        } else if (firstExists) {
            // 정식 플로우: 1차 리뷰만 있으면 ACTIVE 유지
            nextStatus = ParticipationStatus.ACTIVE;
        } else {
            nextStatus = ParticipationStatus.ACTIVE;
        }

        if (creatorCampaign.getStatus() != nextStatus) {
            creatorCampaign.changeStatus(nextStatus);
        }
    }

    /**
     * 캠페인에 필요한 모든 ContentType에 대해 1차 리뷰가 완료되었는지 확인
     *
     * @param creatorCampaignId 크리에이터 캠페인 ID
     * @param campaign 캠페인 엔티티
     * @return 모든 필수 1차 리뷰가 완료되었으면 true
     */
    private boolean checkAllFirstReviewsComplete(Long creatorCampaignId, Campaign campaign) {
        List<ContentType> requiredTypes = new ArrayList<>();

        if (campaign.getFirstContentPlatform() != null) {
            requiredTypes.add(campaign.getFirstContentPlatform());
        }

        if (campaign.getSecondContentPlatform() != null) {
            requiredTypes.add(campaign.getSecondContentPlatform());
        }

        // 업로드된 1차 리뷰의 ContentType 목록 조회
        List<ContentType> uploadedTypes = campaignReviewGetService
            .findContentTypesByRound(creatorCampaignId,
                com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound.FIRST);

        // 모든 필수 타입이 업로드되었는지 확인
        return uploadedTypes.containsAll(requiredTypes);
    }

}
