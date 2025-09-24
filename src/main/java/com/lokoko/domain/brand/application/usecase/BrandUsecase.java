package com.lokoko.domain.brand.application.usecase;

import com.lokoko.domain.brand.api.dto.request.BrandInfoUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandMyPageUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandProfileImageRequest;
import com.lokoko.domain.brand.api.dto.response.BrandMyPageResponse;
import com.lokoko.domain.brand.api.dto.response.BrandProfileAndStatisticsResponse;
import com.lokoko.domain.brand.api.dto.response.BrandProfileImageResponse;
import com.lokoko.domain.brand.application.service.BrandGetService;
import com.lokoko.domain.brand.application.service.BrandUpdateService;
import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.campaign.api.dto.response.CampaignParticipatedResponse;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
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

    private final BrandUpdateService brandUpdateService;

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
    public List<CampaignParticipatedResponse> getCampaignTitles(Long brandId) {
        Brand brand = brandGetService.getBrandById(brandId);

        return campaignGetService.getInReviewCampaignTitles(brand);
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
