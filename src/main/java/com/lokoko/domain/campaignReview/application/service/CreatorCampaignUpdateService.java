package com.lokoko.domain.campaignReview.application.service;

import static com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.creatorCampaign.domain.repository.CreatorCampaignRepository;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.global.config.BetaFeatureConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreatorCampaignUpdateService {

	private final CreatorCampaignRepository creatorCampaignRepository;
	private final CampaignReviewGetService campaignReviewGetService;
	private final BetaFeatureConfig betaFeatureConfig;
    private final CreatorCampaignStatusResolver creatorCampaignStatusResolver;

	@Transactional
	public void refreshParticipationStatus(Long creatorCampaignId) {
		CreatorCampaign creatorCampaign = creatorCampaignRepository.getByIdForUpdate(creatorCampaignId);
		Campaign campaign = creatorCampaign.getCampaign();

		boolean firstExists = campaignReviewGetService.existsFirst(creatorCampaignId);
		boolean secondExists = campaignReviewGetService.existsSecond(creatorCampaignId);
		List<ContentType> firstRoundContentTypes = campaignReviewGetService
			.findContentTypesByRound(
				creatorCampaignId,
				FIRST
			);

		ParticipationStatus nextStatus = creatorCampaignStatusResolver.determineNextStatus(
			creatorCampaign,
			campaign,
			betaFeatureConfig.isSimplifiedReviewFlow(),
			firstExists,
			secondExists,
			firstRoundContentTypes
		);

		if (creatorCampaign.getStatus() != nextStatus) {
			creatorCampaign.changeStatus(nextStatus);
		}
	}
}
