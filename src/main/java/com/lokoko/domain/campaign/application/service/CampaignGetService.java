package com.lokoko.domain.campaign.application.service;

import com.lokoko.domain.brand.api.dto.request.ApplicantStatus;
import com.lokoko.domain.brand.api.dto.response.BrandDashboardCampaignListResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignInfoListResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignListResponse;
import com.lokoko.domain.brand.api.dto.response.CampaignApplicantListResponse;
import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.campaign.api.dto.response.*;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignDetailPageStatus;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductTypeFilter;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatusFilter;
import com.lokoko.domain.campaign.domain.entity.enums.LanguageFilter;
import com.lokoko.domain.campaign.domain.repository.CampaignRepository;
import com.lokoko.domain.campaign.exception.CampaignNotFoundException;
import com.lokoko.domain.campaign.exception.NotCampaignOwnershipException;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.domain.entity.enums.CreatorStatus;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.repository.CreatorCampaignRepository;
import com.lokoko.domain.media.image.domain.repository.CampaignImageRepository;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.domain.user.exception.UserNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.lokoko.domain.user.domain.entity.enums.Role;
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
    private final UserRepository userRepository;

    private final CampaignStatusManager campaignStatusManager;

    public Campaign findByCampaignId(Long campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);
    }

    public CampaignDetailResponse getCampaignDetail(Long userId, Long campaignId) {
        Campaign campaign = findCampaignAndInitializeCollection(campaignId);
        List<CampaignImageResponse> topImages = campaignImageRepository.findThumbnailImagesByCampaignId(campaignId);
        List<CampaignImageResponse> bottomImages = campaignImageRepository.findDetailImagesByCampaignId(campaignId);

        CampaignDetailPageStatus detailPageStatus = determineDetailPageStatus(userId, campaign);

        String currentUserRole = null; // 비로그인 유저
        String creatorRoleInfo = null;

        if (userId != null) {
            User currentUser = userRepository.findById(userId).get();
            currentUserRole = currentUser.getRole().name();
            // user 타입 확인
            if (currentUserRole.equals(Role.CREATOR.name())) {

                Creator currentCreator = currentUser.getCreator();

                if (currentCreator.getCreatorType() != null){
                    creatorRoleInfo = currentCreator.getCreatorType().name();
                }

                if (currentCreator.getCreatorStatus() == CreatorStatus.NOT_APPROVED){
                    creatorRoleInfo = CreatorStatus.NOT_APPROVED.name();
                }

            }
        }

        return CampaignDetailResponse.of(campaign, topImages, bottomImages,
                detailPageStatus, currentUserRole, creatorRoleInfo);
    }

    public List<Campaign> getBrandIssuedCampaignsInReview(Brand brand) {
        return campaignRepository.findAllByBrandAndCampaignStatusOrderByTitleAsc(
                brand, CampaignStatus.IN_REVIEW);
    }

    private Campaign findCampaignAndInitializeCollection(Long campaignId) {
        Campaign campaign = campaignRepository.findCampaignWithBrandById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);
        initializeElementCollections(campaign);
        return campaign;
    }

    private CampaignDetailPageStatus determineDetailPageStatus(Long userId, Campaign campaign) {
        if (userId == null) {
            return campaignStatusManager.determineStatusForNonLoggedInAndCustomer(campaign);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return switch (user.getRole()) {
            case PENDING, CUSTOMER -> campaignStatusManager.determineStatusForNonLoggedInAndCustomer(campaign);
            case BRAND, ADMIN -> campaignStatusManager.determineStatusForBrandAndAdmin(campaign);
            case CREATOR -> determineStatusForCreator(campaign, user);
        };
    }

    private CampaignDetailPageStatus determineStatusForCreator(Campaign campaign, User user) {
        Long creatorId = user.getCreator().getId();
        Optional<CreatorCampaign> creatorCampaign = creatorCampaignRepository
                .findByCreatorIdAndCampaignId(creatorId, campaign.getId());

        return campaignStatusManager.determineStatusInDetailPage(campaign, creatorCampaign);
    }

    private static void initializeElementCollections(Campaign campaign) {
        Hibernate.initialize(campaign.getParticipationRewards());
        Hibernate.initialize(campaign.getDeliverableRequirements());
        Hibernate.initialize(campaign.getEligibilityRequirements());
    }

    public MainPageCampaignListResponse getCampaignsInMainPage(LanguageFilter lang, CampaignProductTypeFilter category, int page, int size) {
        return campaignRepository.findCampaignsInMainPage(lang, category, PageRequest.of(page, size));
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

        if (!draftCampaign.getBrand().getId().equals(brandId)){
            throw new NotCampaignOwnershipException();
        }
        initializeElementCollections(draftCampaign);

        List<CampaignImageResponse> thumbnailImages = campaignImageRepository.findThumbnailImagesByCampaignId(campaignId);
        List<CampaignImageResponse> detailImages = campaignImageRepository.findDetailImagesByCampaignId(campaignId);

        return CampaignBasicResponse.of(draftCampaign, thumbnailImages, detailImages);
    }

    /**
     * 브랜드 마이페이지 승인대기중 캠페인 조회
     */
    public CampaignBasicResponse getWaitingApprovalCampaign(Long brandId, Long campaignId) {

        Campaign waitingApprovalCampaign = campaignRepository.findWaitingApprovalCampaignById(campaignId, CampaignStatus.WAITING_APPROVAL)
                .orElseThrow(CampaignNotFoundException::new);

        if (!waitingApprovalCampaign.getBrand().getId().equals(brandId)){
            throw new NotCampaignOwnershipException();
        }
        initializeElementCollections(waitingApprovalCampaign);

        List<CampaignImageResponse> thumbnailImages = campaignImageRepository.findThumbnailImagesByCampaignId(campaignId);
        List<CampaignImageResponse> detailImages = campaignImageRepository.findDetailImagesByCampaignId(campaignId);

        return CampaignBasicResponse.of(waitingApprovalCampaign, thumbnailImages, detailImages);
    }

    public BrandMyCampaignInfoListResponse getSimpleCampaignInfos(Long brandId) {
        return campaignRepository.findSimpleCampaignInfoByBrandId(brandId);
    }

    public CampaignApplicantListResponse getCampaignApplicants(Long brandId, Long campaignId, int page, int size, ApplicantStatus status) {

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        if (!campaign.getBrand().getId().equals(brandId)) {
            throw new NotCampaignOwnershipException();
        }

        return creatorCampaignRepository.findCampaignApplicants(brandId, campaignId, PageRequest.of(page, size), status);
    }

    public int countOngoingCampaigns(Long brandId, Instant now) {

        return campaignRepository.countOngoingCampaignsById(brandId, now);
    }

    public int countCompletedCampaigns(Long brandId, Instant now) {

        return campaignRepository.countCompletedCampaignsById(brandId, now);
    }

    public List<CampaignParticipatedResponse> getInReviewCampaignTitles(Brand brand) {
        return campaignRepository.findInReviewCampaignTitlesByBrand(brand);
    }

    /**
     * 브랜드 대시보드 캠페인 목록 조회
     */
    public BrandDashboardCampaignListResponse getBrandDashboardCampaigns(Long brandId, int page, int size) {
        return campaignRepository.findBrandDashboardCampaigns(brandId, PageRequest.of(page, size));
    }

    public AdminCampaignBasicResponse getCampaignDetailForAdmin(Long campaignId) {
        Campaign campaign  = campaignRepository.findById(campaignId)
                .orElseThrow(CampaignNotFoundException::new);

        initializeElementCollections(campaign);

        List<CampaignImageResponse> thumbnailImages = campaignImageRepository.findThumbnailImagesByCampaignId(campaignId);
        List<CampaignImageResponse> detailImages = campaignImageRepository.findDetailImagesByCampaignId(campaignId);

        return AdminCampaignBasicResponse.of(campaign, thumbnailImages, detailImages);
    }
}
