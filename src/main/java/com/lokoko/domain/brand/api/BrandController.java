package com.lokoko.domain.brand.api;

import com.lokoko.domain.brand.api.dto.request.BrandInfoUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandNoteRevisionRequest;
import com.lokoko.domain.brand.api.dto.response.BrandNoteRevisionResponse;
import com.lokoko.domain.brand.api.message.ResponseMessage;
import com.lokoko.domain.brand.application.BrandService;
import com.lokoko.domain.campaign.api.dto.request.CampaignDraftRequest;
import com.lokoko.domain.campaign.api.dto.request.CampaignPublishRequest;
import com.lokoko.domain.campaign.api.dto.response.CampaignCreateResponse;
import com.lokoko.domain.campaign.application.service.CampaignService;
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

@Tag(name = "BRAND")
@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;
    private final CampaignService campaignService;
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
    public ApiResponse<CampaignCreateResponse> createDraftCampaign(
            @Parameter(hidden = true) @CurrentUser Long brandId,
            @Valid @RequestBody CampaignDraftRequest draftRequest) {

        CampaignCreateResponse response = campaignService.createCampaignDraft(brandId, draftRequest);
        return ApiResponse.success(HttpStatus.OK,
                ResponseMessage.CAMPAIGN_DRAFT_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "캠페인 생성 - 발행",
            description = "브랜드 마이페이지에서 브랜드가 캠페인을 발행 상태로 생성하는 API 입니다.")
    @PostMapping("/my/campaigns/publish")
    public ApiResponse<CampaignCreateResponse> createAndPublishCampaign(
            @Parameter(hidden = true) @CurrentUser Long brandId,
            @Valid @RequestBody CampaignPublishRequest publishRequest) {

        CampaignCreateResponse response = campaignService.createAndPublishCampaign(brandId, publishRequest);
        return ApiResponse.success(HttpStatus.OK,
                ResponseMessage.CAMPAIGN_PUBLISH_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "캠페인 수정 - 임시저장",
            description = "브랜드 마이페이지에서 브랜드가 기존에 존재하는 캠페인에 대한 임시저장을 수행하는 API 입니다.")
    @PutMapping("/my/campaigns/{campaignId}/draft")
    public ApiResponse<CampaignCreateResponse> updateCampaignDraft(
            @Parameter(hidden = true) @CurrentUser Long brandId,
            @PathVariable Long campaignId,
            @Valid @RequestBody CampaignDraftRequest draftRequest) {

        CampaignCreateResponse response = campaignService.updateCampaignToDraft(brandId, campaignId, draftRequest);
        return ApiResponse.success(HttpStatus.OK,
                ResponseMessage.CAMPAIGN_UPDATE_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "캠페인 수정 - 발행",
            description = "브랜드 마이페이지에서 브랜드가 기존에 존재하는 캠페인에 대한 발행을 수행하는 API 입니다.")
    @PatchMapping("/my/campaigns/{campaignId}/publish")
    public ApiResponse<CampaignCreateResponse> publishCampaign(
            @Parameter(hidden = true) @CurrentUser Long brandId,
            @PathVariable Long campaignId,
            @Valid @RequestBody CampaignPublishRequest publishRequest) {

        CampaignCreateResponse response = campaignService.updateAndPublishCampaign(brandId, campaignId, publishRequest);
        return ApiResponse.success(HttpStatus.OK,
                ResponseMessage.CAMPAIGN_PUBLISH_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "브랜드 수정사항 임시저장 / 전달",
            description = "브랜드 마이페이지에서 브랜드가 크리에이터가 올린 1차 리뷰에 대해 수정사항을 남기는 API 입니다.")
    @PostMapping("/my/reviews/{campaignReviewId}/revision-request")
    public ApiResponse<BrandNoteRevisionResponse> requestReviewRevision(
            @PathVariable Long campaignReviewId,
            @RequestParam RevisionAction action,
            @Valid @RequestBody BrandNoteRevisionRequest revisionRequest) {

        String brandNote = campaignReviewUpdateService.requestReviewRevision(action, campaignReviewId, revisionRequest);
        BrandNoteRevisionResponse response = new BrandNoteRevisionResponse(brandNote);

        String message = action == RevisionAction.SAVE_DRAFT ? ResponseMessage.REVISION_SAVE_SUCCESS.getMessage() :
                ResponseMessage.REVISION_REQUEST_SUCCESS.getMessage();

        return ApiResponse.success(HttpStatus.OK, message, response);
    }
}
