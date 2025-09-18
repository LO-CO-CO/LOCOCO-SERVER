package com.lokoko.domain.campaign.application.service;

import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignListResponse;
import com.lokoko.domain.brand.domain.repository.BrandRepository;
import com.lokoko.domain.campaign.api.dto.response.*;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignDetailPageStatus;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductTypeFilter;
import com.lokoko.domain.campaign.domain.entity.enums.LanguageFilter;
import com.lokoko.domain.campaign.domain.repository.CampaignRepository;
import com.lokoko.domain.campaign.exception.CampaignNotFoundException;
import com.lokoko.domain.campaign.exception.NotCampaignOwnershipException;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.repository.CreatorCampaignRepository;
import com.lokoko.domain.image.domain.repository.CampaignImageRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        List<CampaignImageResponse> topImages = campaignImageRepository.findTopImagesByCampaignId(campaignId);
        List<CampaignImageResponse> bottomImages = campaignImageRepository.findBottomImagesByCampaignId(campaignId);

        //캠페인 상세페이지를 조회하는 크리에이터가 캠페인에 참여하지 않았을 수도 있으므로 Optional 을 반환
        Optional<CreatorCampaign> creatorCampaign = creatorCampaignRepository.findByCreatorIdAndCampaignId(creatorId,
                campaignId);
        CampaignDetailPageStatus campaignStatus = campaignStatusManager.determineStatusInDetailPage(campaign,
                creatorCampaign);

        return CampaignDetailResponse.of(campaign, topImages, bottomImages, campaignStatus);
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
    public BrandMyCampaignListResponse getBrandMyCampaigns(Long brandId, CampaignStatus status, int page, int size) {
        return campaignRepository.findBrandMyCampaigns(brandId, status, PageRequest.of(page, size));
    }

    /**
     * 브랜드 마이페이지 임시저장 캠페인 조회
     */
    public CampaignBasicResponse getDraftCampaign(Long brandId, Long campaignId) {

        Campaign draftCampaign = campaignRepository.findDraftCampaignById(campaignId, CampaignStatus.DRAFT)
                .orElseThrow(CampaignNotFoundException::new);

        if (!draftCampaign.getBrand().getId().equals(brandId)){
            throw new NotCampaignOwnershipException();
        }
        initializeElementCollections(draftCampaign);

        List<CampaignImageResponse> topImages = campaignImageRepository.findTopImagesByCampaignId(campaignId);
        List<CampaignImageResponse> bottomImages = campaignImageRepository.findBottomImagesByCampaignId(campaignId);

        return CampaignBasicResponse.of(draftCampaign, topImages, bottomImages);
    }
}
