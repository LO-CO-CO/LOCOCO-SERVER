package com.lokoko.domain.brand;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lokoko.domain.brand.application.service.BrandGetService;
import com.lokoko.domain.brand.application.service.BrandUpdateService;
import com.lokoko.domain.brand.application.usecase.BrandUsecase;
import com.lokoko.domain.campaign.application.mapper.CampaignMapper;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaignReview.application.mapper.CampaignReviewMapper;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewGetService;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewStatusManager;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.media.socialclip.application.service.SocialClipGetService;
import com.lokoko.global.config.BetaFeatureConfig;

@ExtendWith(MockitoExtension.class)
abstract class BrandUsecaseTestSupport {

	@Mock
	protected BrandGetService brandGetService;

	@Mock
	protected CampaignGetService campaignGetService;

	@Mock
	protected CreatorCampaignGetService creatorCampaignGetService;

	@Mock
	protected CampaignReviewGetService campaignReviewGetService;

	@Mock
	protected SocialClipGetService socialClipGetService;

	@Mock
	protected BrandUpdateService brandUpdateService;

	@Mock
	protected CampaignReviewStatusManager campaignReviewStatusManager;

	@Mock
	protected BetaFeatureConfig betaFeatureConfig;

	@Mock
	protected CampaignMapper campaignMapper;

	@Mock
	protected CampaignReviewMapper campaignReviewMapper;

	@InjectMocks
	protected BrandUsecase brandUsecase;
}
