package com.lokoko.domain.user.application.usecase;

import com.lokoko.domain.campaign.api.dto.response.CampaignBasicResponse;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.domain.repository.CampaignRepository;
import com.lokoko.domain.user.api.dto.request.ApproveCampaignIdsRequest;
import com.lokoko.domain.user.api.dto.request.ApprovedStatus;
import com.lokoko.domain.user.api.dto.request.CampaignModifyRequest;
import com.lokoko.domain.user.api.dto.request.DeleteCampaignIdsRequest;
import com.lokoko.domain.user.api.dto.response.AdminCampaignListResponse;
import com.lokoko.domain.user.application.service.*;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.global.utils.AdminValidator;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUsecase {

    private final UserGetService userGetService;
    private final CampaignGetService campaignGetService;

    private final AdminCampaignUpdateService adminCampaignUpdateService;
    private final AdminCreatorUpdateService adminCreatorUpdateService;
    private final AdminReviewDeleteService adminReviewDeleteService;
    private final AdminCampaignDeleteService adminCampaignDeleteService;

    private final CampaignRepository campaignRepository;

    private void validateIsAdmin(Long userId) {
        User user = userGetService.findUserById(userId);
        AdminValidator.validateAdminRole(user);
    }

    @Transactional
    public void approveCampaign(Long userId, Long campaignId) {
        validateIsAdmin(userId);
        campaignGetService.findByCampaignId(campaignId);
        adminCampaignUpdateService.approve(campaignId, Instant.now());
    }

    @Transactional
    public void approveCreator(Long adminUserId, Long userId) {
        validateIsAdmin(adminUserId);
        adminCreatorUpdateService.approveById(userId, Instant.now());
    }

    @Transactional
    public void deleteReviewByAdmin(Long userId, Long campaignReviewId) {
        validateIsAdmin(userId);
        adminReviewDeleteService.deleteReview(userId, campaignReviewId);
    }

    @Transactional
    public void approveCampaigns(Long userId, ApproveCampaignIdsRequest request) {
        validateIsAdmin(userId);
        adminCampaignUpdateService.approveCampaigns(request.campaignIds());
    }

    @Transactional
    public void deleteCampaigns(Long userId, DeleteCampaignIdsRequest request) {
        validateIsAdmin(userId);
        adminCampaignDeleteService.deleteCampaigns(request.campaignIds());
    }

    public AdminCampaignListResponse findAllCampaigns(Long userId, ApprovedStatus status, int page, int size) {
        validateIsAdmin(userId);
        return campaignRepository.findAllCampaignsByAdmin(status, PageRequest.of(page,size));
    }

    public CampaignBasicResponse findCampaignDetail(Long userId, Long campaignId) {
        validateIsAdmin(userId);
        return campaignGetService.getCampaignDetailForAdmin(campaignId);
    }

    @Transactional
    public void modifyCampaign(Long userId, Long campaignId, CampaignModifyRequest request) {
        validateIsAdmin(userId);
        adminCampaignUpdateService.modifyCampaign(campaignId, request);
    }
}
