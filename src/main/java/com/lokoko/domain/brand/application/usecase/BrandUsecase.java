package com.lokoko.domain.brand.application.usecase;

import com.lokoko.domain.brand.api.dto.request.BrandInfoUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandMyPageUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandProfileImageRequest;
import com.lokoko.domain.brand.api.dto.response.BrandIssuedCampaignResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyPageResponse;
import com.lokoko.domain.brand.api.dto.response.BrandProfileAndStatisticsResponse;
import com.lokoko.domain.brand.api.dto.response.BrandProfileImageResponse;
import com.lokoko.domain.brand.application.service.BrandGetService;
import com.lokoko.domain.brand.application.service.BrandUpdateService;
import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.campaign.application.mapper.CampaignMapper;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaignReview.api.dto.response.CampaignReviewDetailListResponse;
import com.lokoko.domain.campaignReview.api.dto.response.CampaignReviewDetailResponse;
import com.lokoko.domain.campaignReview.application.mapper.CampaignReviewMapper;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewGetService;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewStatusManager;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BrandUsecase {

    private final BrandGetService brandGetService;
    private final CampaignGetService campaignGetService;
    private final CreatorCampaignGetService creatorCampaignGetService;
    private final CampaignReviewGetService campaignReviewGetService;

    private final BrandUpdateService brandUpdateService;

    private final CampaignReviewStatusManager campaignReviewStatusManager;

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
    public CampaignReviewDetailListResponse getCreatorCampaignReview(Long brandId, Long campaignId, Long creatorId) {
        brandGetService.getBrandById(brandId);
        Campaign campaign = campaignGetService.findByCampaignId(campaignId);
        CreatorCampaign creatorCampaign = creatorCampaignGetService.getByCampaignAndCreatorId(campaign, creatorId);
        ReviewRound round = campaignReviewStatusManager.determineReviewRound(campaign.getCampaignStatus(),
                creatorCampaign.getStatus());

        List<CampaignReview> reviews = campaignReviewGetService.getAllByCreatorCampaignAndRound(creatorCampaign, round);

        List<CampaignReviewDetailResponse> reviewResponses = reviews.stream()
                .map(review -> campaignReviewMapper.toDetailResponse(
                        review, campaignReviewGetService.getOrderedMediaUrls(review)))
                .toList();

        return campaignReviewMapper.toDetailListResponse(campaign, round, reviewResponses);
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
}
