package com.lokoko.domain.brand;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Answers.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.lokoko.domain.brand.api.dto.request.BrandMyPageUpdateRequest;
import com.lokoko.domain.brand.api.dto.response.BrandIssuedCampaignResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyPageResponse;
import com.lokoko.domain.brand.api.dto.response.BrandProfileAndStatisticsResponse;
import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.exception.NotCampaignOwnershipException;
import com.lokoko.domain.campaignReview.api.dto.response.CampaignReviewDetailListResponse;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creator.api.dto.response.CreatorInfo;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;

@DisplayName("Brand 마이페이지 Usecase 테스트")
class BrandMyPageTest extends BrandUsecaseTestSupport {

	private Long brandId;
	private Brand brand;

	@BeforeEach
	void setUp() {
		brandId = 1L;
		brand = mock(Brand.class, RETURNS_DEEP_STUBS);
	}

	@Nested
	@DisplayName("브랜드 마이페이지 조회")
	class GetBrandMyPage {

		@Test
		@DisplayName("getBrandMyPage() : 브랜드와 유저 정보를 조회해 마이페이지 응답을 반환한다")
		void getBrandMyPage_success() {
			// given
			given(brandGetService.getBrandWithUserById(brandId)).willReturn(brand);

			// when
			BrandMyPageResponse result = brandUsecase.getBrandMyPage(brandId);

			// then
			BrandMyPageResponse expected = BrandMyPageResponse.from(brand, brand.getUser());

			assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expected);
		}

		@Test
		@DisplayName("getBrandProfileAndStatistics() : 진행 중/종료 캠페인 수를 포함한 프로필 통계를 반환한다")
		void getBrandProfileAndStatistics_success() {
			// given
			given(brandGetService.getBrandById(brandId)).willReturn(brand);
			given(campaignGetService.countOngoingCampaigns(eq(brandId), any(Instant.class))).willReturn(3);
			given(campaignGetService.countCompletedCampaigns(eq(brandId), any(Instant.class))).willReturn(7);

			// when
			BrandProfileAndStatisticsResponse result = brandUsecase.getBrandProfileAndStatistics(brandId);

			// then
			BrandProfileAndStatisticsResponse expected =
				BrandProfileAndStatisticsResponse.of(brand, 3, 7);

			assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expected);
		}
	}

	@Nested
	@DisplayName("브랜드 발행 캠페인 및 리뷰 조회")
	class ReviewAndCampaignQuery {

		@Test
		@DisplayName("getMyIssuedCampaignsInReview() : 검토 중인 캠페인 목록을 응답 DTO 리스트로 반환한다")
		void getMyIssuedCampaignsInReview_success() {
			// given
			Campaign firstCampaign = mock(Campaign.class);
			Campaign secondCampaign = mock(Campaign.class);
			BrandIssuedCampaignResponse firstResponse = mock(BrandIssuedCampaignResponse.class);
			BrandIssuedCampaignResponse secondResponse = mock(BrandIssuedCampaignResponse.class);

			given(brandGetService.getBrandById(brandId)).willReturn(brand);
			given(campaignGetService.getBrandIssuedCampaignsInReview(brand))
				.willReturn(List.of(firstCampaign, secondCampaign));
			given(campaignMapper.toBrandIssuedCampaignResponse(firstCampaign)).willReturn(firstResponse);
			given(campaignMapper.toBrandIssuedCampaignResponse(secondCampaign)).willReturn(secondResponse);

			// when
			List<BrandIssuedCampaignResponse> result = brandUsecase.getMyIssuedCampaignsInReview(brandId);

			// then
			assertThat(result).containsExactly(firstResponse, secondResponse);
		}

		@SuppressWarnings("unchecked")
		@Test
		@DisplayName("getCreatorCampaignReview() : 브랜드 소유 캠페인의 리뷰 상세를 조립해 mapper에 전달한다")
		void getCreatorCampaignReview_success() {
			// given
			Long campaignReviewId = 100L;
			CampaignReview campaignReview = mock(CampaignReview.class);
			CreatorCampaign creatorCampaign = mock(CreatorCampaign.class);
			Campaign campaign = mock(Campaign.class, RETURNS_DEEP_STUBS);
			Creator creator = mock(Creator.class, RETURNS_DEEP_STUBS);
			CampaignReviewDetailListResponse expectedResponse = mock(CampaignReviewDetailListResponse.class);

			Instant reviewRequestedAt = Instant.parse("2026-03-07T00:00:00Z");
			List<String> mediaUrls = List.of("https://cdn.test/1.jpg", "https://cdn.test/2.jpg");

			given(campaignReviewGetService.findById(campaignReviewId)).willReturn(campaignReview);
			given(campaignReview.getCreatorCampaign()).willReturn(creatorCampaign);
			given(creatorCampaign.getCampaign()).willReturn(campaign);
			given(creatorCampaign.getCreator()).willReturn(creator);
			given(campaign.getBrand().getId()).willReturn(brandId);

			given(campaignReview.getReviewRound()).willReturn(ReviewRound.SECOND);
			given(campaignReview.getPostUrl()).willReturn("https://instagram.com/p/test");
			given(campaignReview.getRevisionRequestedAt()).willReturn(reviewRequestedAt);
			given(campaignReviewGetService.getOrderedMediaUrls(campaignReview)).willReturn(mediaUrls);

			given(creator.getId()).willReturn(55L);
			given(creator.getCreatorName()).willReturn("creator-nickname");
			given(creator.getUser().getName()).willReturn("홍길동");
			given(creator.getUser().getProfileImageUrl()).willReturn("https://cdn.test/profile.jpg");

			given(campaignReviewMapper.toDetailListResponse(
				eq(campaign),
				eq(campaignReview),
				any(ReviewRound.class),
				anyList(),
				any(),
				any(CreatorInfo.class),
				any(Instant.class)
			)).willReturn(expectedResponse);

			// when
			CampaignReviewDetailListResponse result =
				brandUsecase.getCreatorCampaignReview(brandId, campaignReviewId);

			// then
			ArgumentCaptor<ReviewRound> reviewRoundCaptor = ArgumentCaptor.forClass(ReviewRound.class);
			ArgumentCaptor<List<String>> mediaUrlsCaptor = ArgumentCaptor.forClass(List.class);
			ArgumentCaptor<String> postUrlCaptor = ArgumentCaptor.forClass(String.class);
			ArgumentCaptor<CreatorInfo> creatorInfoCaptor = ArgumentCaptor.forClass(CreatorInfo.class);
			ArgumentCaptor<Instant> reviewRequestedAtCaptor = ArgumentCaptor.forClass(Instant.class);

			then(campaignReviewMapper).should().toDetailListResponse(
				eq(campaign),
				eq(campaignReview),
				reviewRoundCaptor.capture(),
				mediaUrlsCaptor.capture(),
				postUrlCaptor.capture(),
				creatorInfoCaptor.capture(),
				reviewRequestedAtCaptor.capture()
			);

			CreatorInfo expectedCreatorInfo = CreatorInfo.builder()
				.creatorId(55L)
				.creatorFullName("홍길동")
				.creatorNickname("creator-nickname")
				.profileImageUrl("https://cdn.test/profile.jpg")
				.build();

			assertThat(result).isSameAs(expectedResponse);
			assertThat(reviewRoundCaptor.getValue()).isEqualTo(ReviewRound.SECOND);
			assertThat(mediaUrlsCaptor.getValue()).containsExactlyElementsOf(mediaUrls);
			assertThat(postUrlCaptor.getValue()).isEqualTo("https://instagram.com/p/test");
			assertThat(creatorInfoCaptor.getValue())
				.usingRecursiveComparison()
				.isEqualTo(expectedCreatorInfo);
			assertThat(reviewRequestedAtCaptor.getValue()).isEqualTo(reviewRequestedAt);
		}

		@Test
		@DisplayName("getCreatorCampaignReview() : 다른 브랜드의 캠페인 리뷰면 예외를 던진다")
		void getCreatorCampaignReview_notOwner_throwsException() {
			// given
			Long campaignReviewId = 100L;
			CampaignReview campaignReview = mock(CampaignReview.class);
			CreatorCampaign creatorCampaign = mock(CreatorCampaign.class);
			Campaign campaign = mock(Campaign.class, RETURNS_DEEP_STUBS);

			given(campaignReviewGetService.findById(campaignReviewId)).willReturn(campaignReview);
			given(campaignReview.getCreatorCampaign()).willReturn(creatorCampaign);
			given(creatorCampaign.getCampaign()).willReturn(campaign);
			given(campaign.getBrand().getId()).willReturn(999L);

			// when & then
			assertThatThrownBy(() -> brandUsecase.getCreatorCampaignReview(brandId, campaignReviewId))
				.isInstanceOf(NotCampaignOwnershipException.class);
		}
	}

	@Nested
	@DisplayName("브랜드 마이페이지 수정")
	class UpdateBrandMyPage {

		@Test
		@DisplayName("updateBrandMyPage() : 브랜드를 조회한 뒤 마이페이지 정보를 수정한다")
		void updateBrandMyPage_success() {
			// given
			BrandMyPageUpdateRequest request = mock(BrandMyPageUpdateRequest.class);
			given(brandGetService.getBrandById(brandId)).willReturn(brand);

			// when & then
			assertThatCode(() -> brandUsecase.updateBrandMyPage(brandId, request))
				.doesNotThrowAnyException();

			then(brandUpdateService).should().updateBrandMyPage(brand, request);
		}
	}
}