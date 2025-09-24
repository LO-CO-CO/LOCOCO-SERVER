package com.lokoko.domain.campaign.application.service;

import com.lokoko.domain.brand.api.dto.response.BrandDashboardCampaignListResponse;
import com.lokoko.domain.brand.api.dto.response.BrandDashboardCampaignResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignInfoListResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignListResponse;
import com.lokoko.domain.brand.api.dto.response.CampaignApplicantListResponse;
import com.lokoko.domain.brand.api.dto.response.CampaignDashboard;
import com.lokoko.domain.campaign.api.dto.response.CampaignBasicResponse;
import com.lokoko.domain.campaign.api.dto.response.CampaignDetailResponse;
import com.lokoko.domain.campaign.api.dto.response.CampaignImageResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageCampaignListResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageUpcomingCampaignListResponse;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignDetailPageStatus;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductTypeFilter;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatusFilter;
import com.lokoko.domain.campaign.domain.entity.enums.LanguageFilter;
import com.lokoko.domain.campaign.domain.repository.CampaignRepository;
import com.lokoko.domain.campaign.exception.CampaignNotFoundException;
import com.lokoko.domain.campaign.exception.NotCampaignOwnershipException;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.repository.CreatorCampaignRepository;
import com.lokoko.domain.image.domain.repository.CampaignImageRepository;
import com.lokoko.global.common.response.PageableResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignGetService {

    private final CampaignRepository campaignRepository;
    private final CampaignImageRepository campaignImageRepository;
    private final CreatorCampaignRepository creatorCampaignRepository;

    private final CampaignStatusManager campaignStatusManager;

    public Campaign findByCampaignId(Long campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);
    }

    public CampaignDetailResponse getCampaignDetail(Long creatorId, Long campaignId) {

        Campaign campaign = campaignRepository.findCampaignWithBrandById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        initializeElementCollections(campaign);

        List<CampaignImageResponse> thumbnailImages = campaignImageRepository.findThumbnailImagesByCampaignId(campaignId);
        List<CampaignImageResponse> detailImages = campaignImageRepository.findDetailImagesByCampaignId(campaignId);

        //캠페인 상세페이지를 조회하는 크리에이터가 캠페인에 참여하지 않았을 수도 있으므로 Optional 을 반환
        Optional<CreatorCampaign> creatorCampaign = creatorCampaignRepository.findByCreatorIdAndCampaignId(creatorId,
                campaignId);
        CampaignDetailPageStatus campaignStatus = campaignStatusManager.determineStatusInDetailPage(campaign,
                creatorCampaign);

        return CampaignDetailResponse.of(campaign, thumbnailImages, detailImages, campaignStatus);
    }

    private static void initializeElementCollections(Campaign campaign) {
        Hibernate.initialize(campaign.getParticipationRewards());
        Hibernate.initialize(campaign.getDeliverableRequirements());
        Hibernate.initialize(campaign.getEligibilityRequirements());
    }

    public MainPageCampaignListResponse getCampaignsInMainPage(Long userId, LanguageFilter lang, CampaignProductTypeFilter category, int page, int size) {
        return campaignRepository.findCampaignsInMainPage(userId, lang, category, PageRequest.of(page, size));
    }

    public MainPageUpcomingCampaignListResponse getUpcomingCampaignsInMainPage(LanguageFilter lang, CampaignProductTypeFilter category) {
        return campaignRepository.findUpcomingCampaignsInMainPage(lang, category);
    }

    /**
     * 브랜드 마이페이지 캠페인 리스트 조회
     **/
    public BrandMyCampaignListResponse getBrandMyCampaigns(Long brandId, CampaignStatusFilter status, int page, int size) {
        return campaignRepository.findBrandMyCampaigns(brandId, status, PageRequest.of(page, size));
    }

    /**
     * 브랜드 마이페이지 임시저장 캠페인 조회
     */
    public CampaignBasicResponse getDraftCampaign(Long brandId, Long campaignId) {

        Campaign draftCampaign = campaignRepository.findDraftCampaignById(campaignId, CampaignStatus.DRAFT)
                .orElseThrow(CampaignNotFoundException::new);

        if (!draftCampaign.getBrand().getId().equals(brandId)) {
            throw new NotCampaignOwnershipException();
        }
        initializeElementCollections(draftCampaign);

        List<CampaignImageResponse> thumbnailImages = campaignImageRepository.findThumbnailImagesByCampaignId(campaignId);
        List<CampaignImageResponse> detailImages = campaignImageRepository.findDetailImagesByCampaignId(campaignId);

        return CampaignBasicResponse.of(draftCampaign, thumbnailImages, detailImages);
    }


    public BrandMyCampaignInfoListResponse getSimpleCampaignInfos(Long brandId) {
        return campaignRepository.findSimpleCampaignInfoByBrandId(brandId);
    }

    public CampaignApplicantListResponse getCampaignApplicants(Long brandId, Long campaignId, int page, int size) {
        return creatorCampaignRepository.findCampaignApplicants(brandId, campaignId, PageRequest.of(page, size));
    }

    /**
     * 브랜드 대시보드 캠페인 목록 조회
     */
    public BrandDashboardCampaignListResponse getBrandDashboardCampaigns(Long brandId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Repository에서 데이터와 총 개수 조회
        List<CampaignDashboard> campaignList = campaignRepository.findBrandDashboardCampaigns(brandId, pageable);
        Long totalCampaignCount = campaignRepository.countBrandDashboardCampaigns(brandId);

        // CampaignDashboard를 BrandDashboardCampaignResponse로 변환 (상태 계산 포함)
        List<BrandDashboardCampaignResponse> updatedCampaigns = campaignList.stream()
                .map(data -> {
                    // 상태 계산을 위한 임시 Campaign 객체 생성
                    Campaign tempCampaign = Campaign.builder()
                            .id(data.campaignId())
                            .campaignStatus(data.savedStatus())
                            .applyStartDate(data.applyStartDate())
                            .applyDeadline(data.applyDeadline())
                            .creatorAnnouncementDate(data.creatorAnnouncementDate())
                            .reviewSubmissionDeadline(data.reviewSubmissionDeadline())
                            .build();

                    // 현재 시간 기준 상태 계산
                    CampaignStatus campaignCurrentStatus = campaignStatusManager.determineCampaignStatus(tempCampaign);

                    return new BrandDashboardCampaignResponse(
                            data.campaignId(),
                            data.thumbnailUrl(),
                            data.title(),
                            data.applyStartDate(),
                            data.reviewSubmissionDeadline(),
                            // 현재 상태
                            campaignCurrentStatus,
                            data.approvedNumber(),
                            data.instaPostCount(),
                            data.instaReelsCount(),
                            data.tiktokVideoCount()
                    );
                })
                .toList();

        // 페이지 정보 생성
        boolean isLast = (pageable.getOffset() + campaignList.size()) >= totalCampaignCount;
        PageableResponse pageInfo = new PageableResponse(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                campaignList.size(),
                isLast
        );

        return new BrandDashboardCampaignListResponse(updatedCampaigns, pageInfo);
    }
}
