package com.lokoko.domain.campaignReview.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lokoko.domain.campaign.api.dto.response.CampaignParticipatedResponse;
import com.lokoko.domain.campaign.application.mapper.CampaignMapper;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaignReview.api.dto.response.CompletedReviewResponse;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.creatorCampaign.exception.CampaignReviewAbleNotFoundException;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.global.config.BetaFeatureConfig;

@ExtendWith(MockitoExtension.class)
@DisplayName("CampaignReviewReadService 테스트")
class CampaignReviewReadServiceTest {

	@Mock
	private CampaignReviewGetService campaignReviewGetService;

	@Mock
	private CampaignGetService campaignGetService;

	@Mock
	private CreatorCampaignGetService creatorCampaignGetService;

	@Mock
	private CampaignMapper campaignMapper;

	@Mock
	private BetaFeatureConfig betaFeatureConfig;

	@InjectMocks
	private CampaignReviewReadService campaignReviewReadService;

	@Test
	@DisplayName("getMyReviewableCampaign() : 정상 플로우에서는 reviewable 조회 후 브랜드 노트를 읽음 처리한다")
	void getMyReviewableCampaign_marksBrandNotesInNonBetaFlow() {
		Long creatorId = 1L;
		Long campaignId = 10L;

		CreatorCampaign creatorCampaign = mock(CreatorCampaign.class);
		Campaign campaign = mock(Campaign.class);
		CampaignReview noteReview = mock(CampaignReview.class);
		CampaignParticipatedResponse expected = mock(CampaignParticipatedResponse.class);
		CampaignParticipatedResponse.ReviewContentStatus reviewContentStatus =
			mock(CampaignParticipatedResponse.ReviewContentStatus.class);

		given(betaFeatureConfig.isSimplifiedReviewFlow()).willReturn(false);
		given(creatorCampaignGetService.findReviewableInReviewByCampaign(creatorId, campaignId))
			.willReturn(creatorCampaign);
		given(creatorCampaign.getStatus()).willReturn(ParticipationStatus.ACTIVE);
		given(creatorCampaign.getCampaign()).willReturn(campaign);
		given(creatorCampaign.getId()).willReturn(101L);
		given(campaign.getFirstContentPlatform()).willReturn(ContentType.INSTA_REELS);
		given(campaign.getSecondContentPlatform()).willReturn(null);

		given(campaignReviewGetService.getAllByCreatorCampaignAndRound(creatorCampaign, ReviewRound.FIRST))
			.willReturn(List.of(noteReview));
		given(noteReview.getBrandNote()).willReturn("수정해주세요");
		given(noteReview.isNoteViewed()).willReturn(false);

		given(campaignReviewGetService.findContentTypesByRound(101L, ReviewRound.FIRST)).willReturn(List.of());
		given(campaignReviewGetService.findContentTypesByRound(101L, ReviewRound.SECOND)).willReturn(List.of());
		given(campaignMapper.toReviewContentStatus(any(), any(), any(), any(), any(), any()))
			.willReturn(reviewContentStatus);
		given(reviewContentStatus.contentType()).willReturn(ContentType.INSTA_REELS);
		given(campaignMapper.toCampaignParticipationResponse(creatorCampaign, List.of(reviewContentStatus)))
			.willReturn(expected);

		CampaignParticipatedResponse result =
			campaignReviewReadService.getMyReviewableCampaign(creatorId, campaignId, null);

		assertThat(result).isSameAs(expected);
		then(noteReview).should().markNoteAsViewed();
	}

	@Test
	@DisplayName("getCompletedReviews() : 베타 플로우에서는 1차 리뷰를 완료 리뷰로 반환한다")
	void getCompletedReviews_returnsFirstRoundContentsInBetaFlow() {
		Long creatorId = 1L;
		Long campaignId = 10L;

		Campaign campaign = mock(Campaign.class);
		CreatorCampaign creatorCampaign = mock(CreatorCampaign.class);
		CampaignReview firstReview = mock(CampaignReview.class);

		given(betaFeatureConfig.isSimplifiedReviewFlow()).willReturn(true);
		given(campaignGetService.findByCampaignId(campaignId)).willReturn(campaign);
		given(creatorCampaignGetService.getByCampaignAndCreatorId(campaign, creatorId)).willReturn(creatorCampaign);
		given(creatorCampaign.getStatus()).willReturn(ParticipationStatus.COMPLETED);
		given(campaign.getTitle()).willReturn("완료된 캠페인");
		given(creatorCampaign.getId()).willReturn(101L);
		given(campaignReviewGetService.findContentTypesByRound(101L, ReviewRound.FIRST))
			.willReturn(List.of(ContentType.INSTA_REELS));
		given(campaignReviewGetService.findByContentType(101L, ReviewRound.FIRST, ContentType.INSTA_REELS))
			.willReturn(Optional.of(firstReview));
		given(firstReview.getCaptionWithHashtags()).willReturn("캡션");
		given(campaignReviewGetService.getOrderedMediaUrls(firstReview))
			.willReturn(List.of("https://cdn.test/image-1.jpg"));

		CompletedReviewResponse result = campaignReviewReadService.getCompletedReviews(creatorId, campaignId);

		assertThat(result.campaignId()).isEqualTo(campaignId);
		assertThat(result.campaignName()).isEqualTo("완료된 캠페인");
		assertThat(result.reviewContents()).hasSize(1);
		assertThat(result.reviewContents().get(0).contentType()).isEqualTo(ContentType.INSTA_REELS);
		assertThat(result.reviewContents().get(0).captionWithHashtags()).isEqualTo("캡션");
		assertThat(result.reviewContents().get(0).mediaUrls())
			.containsExactly("https://cdn.test/image-1.jpg");
	}

	@Test
	@DisplayName("getCompletedReviews() : COMPLETED 상태가 아니면 예외를 던진다")
	void getCompletedReviews_throwsWhenParticipationIsNotCompleted() {
		Long creatorId = 1L;
		Long campaignId = 10L;

		Campaign campaign = mock(Campaign.class);
		CreatorCampaign creatorCampaign = mock(CreatorCampaign.class);

		given(campaignGetService.findByCampaignId(campaignId)).willReturn(campaign);
		given(creatorCampaignGetService.getByCampaignAndCreatorId(campaign, creatorId)).willReturn(creatorCampaign);
		given(creatorCampaign.getStatus()).willReturn(ParticipationStatus.ACTIVE);

		assertThatThrownBy(() -> campaignReviewReadService.getCompletedReviews(creatorId, campaignId))
			.isInstanceOf(CampaignReviewAbleNotFoundException.class);
	}
}
