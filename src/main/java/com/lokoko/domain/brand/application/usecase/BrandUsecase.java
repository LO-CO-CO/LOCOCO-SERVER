package com.lokoko.domain.brand.application.usecase;

import java.util.List;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lokoko.domain.brand.api.dto.request.BrandInfoUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandMyPageUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandProfileImageRequest;
import com.lokoko.domain.brand.api.dto.response.BrandIssuedCampaignResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyPageResponse;
import com.lokoko.domain.brand.api.dto.response.BrandProfileAndStatisticsResponse;
import com.lokoko.domain.brand.api.dto.response.BrandProfileImageResponse;
import com.lokoko.domain.brand.api.dto.response.CreatorPerformanceResponse;
import com.lokoko.domain.brand.application.service.BrandCreatorPerformanceQueryService;
import com.lokoko.domain.brand.application.service.BrandGetService;
import com.lokoko.domain.brand.application.service.BrandUpdateService;
import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.campaign.application.mapper.CampaignMapper;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.exception.NotCampaignOwnershipException;
import com.lokoko.domain.campaignReview.api.dto.response.CampaignReviewDetailListResponse;
import com.lokoko.domain.campaignReview.application.mapper.CampaignReviewMapper;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewGetService;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creator.api.dto.response.CreatorInfo;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandUsecase {

	private final BrandGetService brandGetService;
	private final CampaignGetService campaignGetService;
	private final CreatorCampaignGetService creatorCampaignGetService;
	private final CampaignReviewGetService campaignReviewGetService;

	private final BrandUpdateService brandUpdateService;
	private final BrandCreatorPerformanceQueryService brandCreatorPerformanceQueryService;

	private final CampaignMapper campaignMapper;
	private final CampaignReviewMapper campaignReviewMapper;

	@Transactional
	public BrandProfileImageResponse createBrandProfilePresignedUrl(Long brandId, BrandProfileImageRequest request) {
		Brand brand = brandGetService.getBrandById(brandId);

		return brandUpdateService.createBrandProfilePresignedUrl(brand, request);
	}

	@Transactional(readOnly = true)
	public BrandMyPageResponse getBrandMyPage(Long brandId) {
		Brand brand = brandGetService.getBrandWithUserById(brandId);

		return BrandMyPageResponse.from(brand, brand.getUser());
	}

	@Transactional(readOnly = true)
	public BrandProfileAndStatisticsResponse getBrandProfileAndStatistics(Long brandId) {
		Brand brand = brandGetService.getBrandById(brandId);

		Instant now = Instant.now();
		int ongoingCampaigns = campaignGetService.countOngoingCampaigns(brandId, now);
		int completedCampaigns = campaignGetService.countCompletedCampaigns(brandId, now);

		return BrandProfileAndStatisticsResponse.of(brand, ongoingCampaigns, completedCampaigns);
	}

	@Transactional(readOnly = true)
	public List<BrandIssuedCampaignResponse> getMyIssuedCampaignsInReview(Long brandId) {
		Brand brand = brandGetService.getBrandById(brandId);
		List<Campaign> campaigns = campaignGetService.getBrandIssuedCampaignsInReview(brand);

		return campaigns.stream()
			.map(campaignMapper::toBrandIssuedCampaignResponse)
			.toList();
	}

	@Transactional(readOnly = true)
	public CampaignReviewDetailListResponse getCreatorCampaignReview(Long brandId, Long campaignReviewId) {

		// 캠페인 리뷰 ID로 캠페인 리뷰 조회
		CampaignReview review = campaignReviewGetService.findById(campaignReviewId);

		// 연관 엔티티들
		CreatorCampaign creatorCampaign = review.getCreatorCampaign();
		Campaign campaign = creatorCampaign.getCampaign();
		Creator creator = creatorCampaign.getCreator();

		// 브랜드 권한 검증 (해당 브랜드가 발행한 캠페인인지 아닌지)
		if (!campaign.getBrand().getId().equals(brandId)) {
			throw new NotCampaignOwnershipException();
		}

		// 현재 라운드 결정 (일단 미사용)
		//ReviewRound round = campaignReviewStatusManager.determineReviewRound(
		//        campaign.getCampaignStatus(),
		//        creatorCampaign.getStatus()
		//);

		// 현재 DB에 저장된 round를 가져와서 검증
		ReviewRound round = review.getReviewRound();

		// 업로드 된 미디어 URL 조회
		List<String> mediaUrls = campaignReviewGetService.getOrderedMediaUrls(review);

		// 만약 2차 리뷰이고, postUrl이 존재한다면 같이 내려주기
		String postUrl = null;
		if (round == ReviewRound.SECOND && review.getPostUrl() != null) {
			postUrl = review.getPostUrl();
		}

		// 크리에이터 정보
		CreatorInfo creatorInfo = CreatorInfo.builder()
			.creatorId(creator.getId())
			.creatorFullName(creator.getUser().getName())
			.creatorNickname(creator.getCreatorName())
			.profileImageUrl(creator.getUser().getProfileImageUrl())
			.build();

		// 검토 요청 시간 (브랜드가 수정 요청한 시간)
		Instant reviewRequestedAt = review.getRevisionRequestedAt();

		return campaignReviewMapper.toDetailListResponse(
			campaign,
			review,
			round,
			mediaUrls,
			postUrl,
			creatorInfo,
			reviewRequestedAt
		);
	}

	@Transactional
	public void updateBrandInfo(Long brandId, BrandInfoUpdateRequest request) {
		Brand brand = brandGetService.getBrandById(brandId);
		brandUpdateService.updateBrandInfo(brand, request);
	}

	@Transactional
	public void updateBrandMyPage(Long brandId, BrandMyPageUpdateRequest request) {
		Brand brand = brandGetService.getBrandById(brandId);
		brandUpdateService.updateBrandMyPage(brand, request);
	}

	/**
	 * 브랜드 컨텐츠 확인 API - 캠페인별 크리에이터 성과 조회
	 */
	@Transactional(readOnly = true)
	public CreatorPerformanceResponse getCreatorPerformances(Long brandId, Long campaignId, int page, int size) {
		Brand brand = brandGetService.getBrandById(brandId);
		Campaign campaign = campaignGetService.findByCampaignId(campaignId);

		validateCampaignOwnership(brand, campaign);

		return brandCreatorPerformanceQueryService.getCreatorPerformances(campaign, page, size);
	}

	private void validateCampaignOwnership(Brand brand, Campaign campaign) {
		if (!campaign.getBrand().getId().equals(brand.getId())) {
			throw new NotCampaignOwnershipException();
		}
	}
}
