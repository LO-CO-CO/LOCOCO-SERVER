package com.lokoko.domain.campaignReview.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lokoko.domain.brand.api.dto.request.BrandNoteRevisionRequest;
import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.campaign.exception.NotCampaignOwnershipException;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.BrandNoteStatus;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewStatus;
import com.lokoko.domain.campaignReview.domain.entity.enums.RevisionAction;
import com.lokoko.domain.campaignReview.exception.RevisionRequestNotAllowedException;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.media.application.service.S3Service;

@ExtendWith(MockitoExtension.class)
@DisplayName("캠페인 리뷰 수정 요청 검증")
class CampaignReviewUpdateServiceTest {

	@Mock
	private S3Service s3Service;

	@Mock
	private CampaignReviewGetService campaignReviewGetService;

	@InjectMocks
	private CampaignReviewUpdateService campaignReviewUpdateService;

	@Test
	@DisplayName("requestReviewRevision() : 소유한 브랜드의 제출된 리뷰에 대해서만 수정 요청을 반영한다")
	void requestReviewRevision_submitsForSubmittedReviewOwnedByBrand() {
		CampaignReview review = submittedReview(7L);
		BrandNoteRevisionRequest request = new BrandNoteRevisionRequest("Please tighten the caption.");

		given(campaignReviewGetService.findById(11L)).willReturn(review);

		var response = campaignReviewUpdateService.requestReviewRevision(
			RevisionAction.SUBMIT, 7L, 11L, request);

		assertThat(response.brandNote()).isEqualTo("Please tighten the caption.");
		assertThat(response.status()).isEqualTo(BrandNoteStatus.PUBLISHED);
		assertThat(response.revisionRequestedAt()).isNotNull();
		assertThat(review.getStatus()).isEqualTo(ReviewStatus.REVISION_REQUESTED);
		assertThat(review.getCreatorCampaign().getStatus()).isEqualTo(ParticipationStatus.ACTIVE);
	}

	@Test
	@DisplayName("requestReviewRevision() : SUBMITTED 상태가 아니면 수정 요청을 거부한다")
	void requestReviewRevision_rejectsNonSubmittedReviews() {
		CampaignReview review = submittedReview(7L);
		review.requestSecondReview("updated caption", "https://example.com/post");

		given(campaignReviewGetService.findById(11L)).willReturn(review);

		assertThatThrownBy(() -> campaignReviewUpdateService.requestReviewRevision(
			RevisionAction.SUBMIT, 7L, 11L, new BrandNoteRevisionRequest("Please revise")))
			.isInstanceOf(RevisionRequestNotAllowedException.class);
	}

	@Test
	@DisplayName("requestReviewRevision() : 소유하지 않은 브랜드면 예외를 던진다")
	void requestReviewRevision_rejectsOtherBrands() {
		CampaignReview review = submittedReview(7L);

		given(campaignReviewGetService.findById(11L)).willReturn(review);

		assertThatThrownBy(() -> campaignReviewUpdateService.requestReviewRevision(
			RevisionAction.SUBMIT, 99L, 11L, new BrandNoteRevisionRequest("Please revise")))
			.isInstanceOf(NotCampaignOwnershipException.class);
	}

	private CampaignReview submittedReview(Long brandId) {
		Brand brand = Brand.builder()
			.id(brandId)
			.brandName("Lokoko")
			.build();

		Campaign campaign = Campaign.builder()
			.brand(brand)
			.brandName(brand.getBrandName())
			.campaignStatus(CampaignStatus.IN_REVIEW)
			.build();

		CreatorCampaign creatorCampaign = CreatorCampaign.builder()
			.campaign(campaign)
			.status(ParticipationStatus.COMPLETED)
			.build();

		CampaignReview review = new CampaignReview();
		review.bindToCreatorCampaign(creatorCampaign);
		review.requestFirstReview("initial caption");
		return review;
	}
}
