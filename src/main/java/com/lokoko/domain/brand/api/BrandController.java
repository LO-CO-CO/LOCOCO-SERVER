package com.lokoko.domain.brand.api;

import com.lokoko.domain.brand.api.dto.request.BrandInfoUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandMyPageUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandNoteRevisionRequest;
import com.lokoko.domain.brand.api.dto.request.BrandProfileImageRequest;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignListResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyPageResponse;
import com.lokoko.domain.brand.api.dto.response.BrandProfileAndStatisticsResponse;
import com.lokoko.domain.brand.api.dto.response.BrandNoteRevisionResponse;
import com.lokoko.domain.brand.api.dto.response.BrandProfileImageResponse;
import com.lokoko.domain.brand.api.message.ResponseMessage;
import com.lokoko.domain.brand.application.BrandService;
import com.lokoko.domain.campaign.api.dto.request.CampaignDraftRequest;
import com.lokoko.domain.campaign.api.dto.request.CampaignPublishRequest;
import com.lokoko.domain.campaign.api.dto.response.CampaignBasicResponse;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.application.service.CampaignService;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatusFilter;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewUpdateService;
import com.lokoko.domain.campaignReview.domain.entity.enums.RevisionAction;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "BRAND")
@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;
    private final CampaignService campaignService;
    private final CampaignGetService campaignGetService;
    private final CampaignReviewUpdateService campaignReviewUpdateService;

    @PatchMapping("/register/info")
    @Operation(summary = "회원가입시 브랜드 추가 정보를 입력하는 API 입니다.")
    public ApiResponse<Void> updateBrandInfo(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid BrandInfoUpdateRequest request) {

        brandService.updateBrandInfo(userId, request);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.BRAND_INFO_UPDATE_SUCCESS.getMessage());
    }

    @Operation(summary = "캠페인 생성 - 임시저장",
            description = "브랜드 마이페이지에서 브랜드가 캠패인을 임시저장 상태로 생성하는 API 입니다.")
    @PostMapping("/my/campaigns/drafts")
    public ApiResponse<CampaignBasicResponse> createDraftCampaign(
            @Parameter(hidden = true) @CurrentUser Long brandId,
            @Valid @RequestBody CampaignDraftRequest draftRequest) {

        CampaignBasicResponse response = campaignService.createCampaignDraft(brandId, draftRequest);
        return ApiResponse.success(HttpStatus.OK,
                ResponseMessage.CAMPAIGN_DRAFT_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "캠페인 생성 - 발행",
            description = "브랜드 마이페이지에서 브랜드가 캠페인을 발행 상태로 생성하는 API 입니다.")
    @PostMapping("/my/campaigns/publish")
    public ApiResponse<CampaignBasicResponse> createAndPublishCampaign(
            @Parameter(hidden = true) @CurrentUser Long brandId,
            @Valid @RequestBody CampaignPublishRequest publishRequest) {

        CampaignBasicResponse response = campaignService.createAndPublishCampaign(brandId, publishRequest);
        return ApiResponse.success(HttpStatus.OK,
                ResponseMessage.CAMPAIGN_PUBLISH_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "캠페인 수정 - 임시저장",
            description = "브랜드 마이페이지에서 브랜드가 기존에 존재하는 캠페인에 대한 임시저장을 수행하는 API 입니다.")
    @PutMapping("/my/campaigns/{campaignId}/draft")
    public ApiResponse<CampaignBasicResponse> updateCampaignDraft(
            @Parameter(hidden = true) @CurrentUser Long brandId,
            @PathVariable Long campaignId,
            @Valid @RequestBody CampaignDraftRequest draftRequest) {

        CampaignBasicResponse response = campaignService.updateCampaignToDraft(brandId, campaignId, draftRequest);
        return ApiResponse.success(HttpStatus.OK,
                ResponseMessage.CAMPAIGN_UPDATE_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "캠페인 수정 - 발행",
            description = "브랜드 마이페이지에서 브랜드가 기존에 존재하는 캠페인에 대한 발행을 수행하는 API 입니다.")
    @PatchMapping("/my/campaigns/{campaignId}/publish")
    public ApiResponse<CampaignBasicResponse> publishCampaign(
            @Parameter(hidden = true) @CurrentUser Long brandId,
            @PathVariable Long campaignId,
            @Valid @RequestBody CampaignPublishRequest publishRequest) {

        CampaignBasicResponse response = campaignService.updateAndPublishCampaign(brandId, campaignId, publishRequest);
        return ApiResponse.success(HttpStatus.OK,
                ResponseMessage.CAMPAIGN_PUBLISH_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "브랜드 수정사항 임시저장 / 전달",
            description = "브랜드 마이페이지에서 브랜드가 크리에이터가 올린 1차 리뷰에 대해 수정사항을 남기는 API 입니다.")
    @PostMapping("/my/reviews/{campaignReviewId}/revision-request")
    public ApiResponse<BrandNoteRevisionResponse> requestReviewRevision(
            @Parameter(hidden = true) @CurrentUser Long brandId,
            @PathVariable Long campaignReviewId,
            @RequestParam RevisionAction action,
            @Valid @RequestBody BrandNoteRevisionRequest revisionRequest) {

        BrandNoteRevisionResponse response = campaignReviewUpdateService.requestReviewRevision(action, brandId, campaignReviewId, revisionRequest);

        String message = action == RevisionAction.SAVE_DRAFT ? ResponseMessage.REVISION_SAVE_SUCCESS.getMessage() :
                ResponseMessage.REVISION_REQUEST_SUCCESS.getMessage();

        return ApiResponse.success(HttpStatus.OK, message, response);
    }

    @Operation(summary = "브랜드 profile image presignedUrl 발급")
    @PostMapping("/profile/image")
    public ApiResponse<BrandProfileImageResponse> createBrandImagePresignedUrl(
            @Parameter(hidden = true) @CurrentUser Long brandId,
            @RequestBody @Valid BrandProfileImageRequest brandProfileImageRequest) {

        BrandProfileImageResponse response = brandService.createBrandProfilePresignedUrl(brandId, brandProfileImageRequest);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.BRAND_PROFILE_IMAGE_PRESIGNED_URL_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "브랜드 마이페이지(프로필) 정보 조회")
    @GetMapping("/profile")
    public ApiResponse<BrandMyPageResponse> getBrandMyPageInfo(
            @Parameter(hidden = true) @CurrentUser Long brandId) {

        BrandMyPageResponse response = brandService.getBrandMyPage(brandId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.BRAND_MYPAGE_INFO_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "브랜드 마이페이지(프로필) 정보 수정")
    @PatchMapping("/profile")
    public ApiResponse<Void> updateBrandMyPageProfile(
            @Parameter(hidden = true) @CurrentUser Long brandId,
            @RequestBody @Valid BrandMyPageUpdateRequest request) {

        brandService.updateBrandMyPage(brandId, request);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.BRAND_UPDATE_MYPAGE_INFO_SUCCESS.getMessage());
    }

    @Operation(summary = "브랜드 마이페이지에서 캠페인 리스트 조회.")
    @GetMapping("/my/campaigns")
    public ApiResponse<BrandMyCampaignListResponse> getBrandMyCampaigns(
            @Parameter(hidden = true) @CurrentUser Long brandId,
            @RequestParam CampaignStatusFilter status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {

        BrandMyCampaignListResponse response = campaignGetService.getBrandMyCampaigns(brandId, status, page, size);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.BRAND_MY_PAGE_CAMPAIGNS_GET_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "브랜드 마이페이지에서 프로필(브랜드 이미지, 이름, 이메일) 및 통계 정보(진행 중인 캠페인, 종료 캠페인)조회")
    @GetMapping("/my/profile/stats")
    public ApiResponse<BrandProfileAndStatisticsResponse> getBrandProfileAndStatistics(@Parameter(hidden = true) @CurrentUser Long brandId) {

        BrandProfileAndStatisticsResponse response = brandService.getBrandProfileAndStatistics(brandId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.BRAND_PROFILE_AND_STATISTICS_GET_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "브랜드 마이페이지에서 임시저장한 캠페인 조회")
    @GetMapping("/my/campaigns/drafts/{campaignId}")
    public ApiResponse<CampaignBasicResponse> getDraftCampaign(
            @Parameter(hidden = true) @CurrentUser Long brandId,
            @PathVariable Long campaignId){

        CampaignBasicResponse response = campaignGetService.getDraftCampaign(brandId, campaignId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.DRAFT_CAMPAIGN_GET_SUCCESS.getMessage(), response);
    }
}
