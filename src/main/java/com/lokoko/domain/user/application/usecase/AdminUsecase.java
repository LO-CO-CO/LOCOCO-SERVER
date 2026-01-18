package com.lokoko.domain.user.application.usecase;

import com.lokoko.domain.campaign.api.dto.response.CampaignBasicResponse;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.domain.repository.CampaignRepository;
import com.lokoko.domain.media.api.dto.request.ProductImagePresignedUrlRequest;
import com.lokoko.domain.media.api.dto.response.ProductImagePresignedUrlResponse;
import com.lokoko.domain.user.api.dto.request.AdminLoginRequest;
import com.lokoko.domain.user.api.dto.request.AdminProductCreateRequest;
import com.lokoko.domain.user.api.dto.request.ApproveCampaignIdsRequest;
import com.lokoko.domain.user.api.dto.request.ApproveCreatorsRequest;
import com.lokoko.domain.user.api.dto.request.CampaignModifyRequest;
import com.lokoko.domain.user.api.dto.request.DeleteCampaignIdsRequest;
import com.lokoko.domain.user.api.dto.request.DeleteCreatorsRequest;
import com.lokoko.domain.user.api.dto.response.AdminCampaignListResponse;
import com.lokoko.domain.user.api.dto.response.AdminCreatorListResponse;
import com.lokoko.domain.user.api.dto.response.AdminLoginResponse;
import com.lokoko.domain.user.api.dto.response.AdminProductCreateResponse;
import com.lokoko.domain.user.application.service.AdminCampaignDeleteService;
import com.lokoko.domain.user.application.service.AdminCampaignUpdateService;
import com.lokoko.domain.user.application.service.AdminCreatorGetService;
import com.lokoko.domain.user.application.service.AdminCreatorUpdateService;
import com.lokoko.domain.user.application.service.AdminLoginService;
import com.lokoko.domain.user.application.service.AdminProductCreateService;
import com.lokoko.domain.user.application.service.AdminReviewDeleteService;
import com.lokoko.domain.user.application.service.UserGetService;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.ApprovedStatus;
import com.lokoko.global.utils.AdminValidator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminUsecase {

    private final UserGetService userGetService;
    private final CampaignGetService campaignGetService;

    private final AdminCreatorGetService adminCreatorGetService;
    private final AdminCampaignUpdateService adminCampaignUpdateService;
    private final AdminCreatorUpdateService adminCreatorUpdateService;
    private final AdminReviewDeleteService adminReviewDeleteService;
    private final AdminCampaignDeleteService adminCampaignDeleteService;
    private final AdminLoginService adminLoginService;
    private final AdminProductCreateService adminProductCreateService;

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
        return campaignRepository.findAllCampaignsByAdmin(status, PageRequest.of(page, size));
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

    public AdminCreatorListResponse findAllCreators(Long userId, int page, int size) {
        validateIsAdmin(userId);
        return adminCreatorGetService.findAllCreators(page, size);
    }

    @Transactional
    public void approveCreators(Long userId, ApproveCreatorsRequest request) {
        validateIsAdmin(userId);
        adminCreatorUpdateService.approveCreators(request.creatorIds(), Instant.now());
    }

    @Transactional
    public void deleteCreators(Long userId, DeleteCreatorsRequest request) {
        validateIsAdmin(userId);
        adminCreatorUpdateService.deleteCreators(request.creatorIds());
    }

    public AdminLoginResponse login(AdminLoginRequest loginRequest, HttpServletResponse servletResponse) {
        return adminLoginService.login(loginRequest, servletResponse);
    }

    @Transactional
    public AdminProductCreateResponse createProduct(Long userId, AdminProductCreateRequest request) {
        validateIsAdmin(userId);
        return adminProductCreateService.createProduct(request);
    }

    @Transactional
    public ProductImagePresignedUrlResponse createProductImagePresignedUrl(
            Long userId,
            ProductImagePresignedUrlRequest request
    ) {
        validateIsAdmin(userId);
        List<String> urls = adminProductCreateService.createPresignedUrlForProductImages(request);

        return ProductImagePresignedUrlResponse.builder()
                .mediaUrl(urls)
                .build();
    }
}
