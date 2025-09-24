package com.lokoko.domain.campaign.api;

import com.lokoko.domain.campaign.api.dto.request.CampaignMediaRequest;
import com.lokoko.domain.campaign.api.dto.response.CampaignDetailResponse;
import com.lokoko.domain.campaign.api.dto.response.CampaignMediaResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageCampaignListResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageUpcomingCampaignListResponse;
import com.lokoko.domain.campaign.api.message.ResponseMessage;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.application.service.CampaignService;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductTypeFilter;
import com.lokoko.domain.campaign.domain.entity.enums.LanguageFilter;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "CAMPAIGN")
@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final CampaignGetService campaignReadService;

    @Operation(summary = "캠페인 상세 조회")
    @GetMapping("/{campaignId}")
    public ApiResponse<CampaignDetailResponse> getCampaignDetail(
            @Parameter(hidden = true) @CurrentUser Long creatorId,
            @PathVariable Long campaignId) {

        CampaignDetailResponse response = campaignReadService.getCampaignDetail(creatorId, campaignId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CAMPAIGN_DETAIL_GET_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "사진 presignedUrl 발급")
    @PostMapping("/media")
    public ApiResponse<CampaignMediaResponse> createMediaPresignedUrl(
            @Parameter(hidden = true) @CurrentUser Long brandId,
            @RequestBody @Valid CampaignMediaRequest mediaRequest) {

        CampaignMediaResponse response = campaignService.createMediaPresignedUrl(brandId, mediaRequest);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CAMPAIGN_MEDIA_PRESIGNED_URL_SUCCESS.getMessage(),
                response);
    }

    @Operation(summary = "메인페이지에서 캠페인 리스트 조회")
    @GetMapping
    public ApiResponse<MainPageCampaignListResponse> getCampaignsInMainPage(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestParam LanguageFilter lang,
            @RequestParam CampaignProductTypeFilter category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        MainPageCampaignListResponse response = campaignReadService.getCampaignsInMainPage(userId, lang, category, page, size);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.MAIN_PAGE_CAMPAIGNS_GET_SUCCESS.getMessage(), response);

    }

    @Operation(summary = "메인페이지 Opening Soon 캠페인 리스트 조회")
    @GetMapping("/upcoming")
    public ApiResponse<MainPageUpcomingCampaignListResponse> getUpcomingCampaignsInMainPage(
            @RequestParam LanguageFilter lang,
            @RequestParam CampaignProductTypeFilter category){

        MainPageUpcomingCampaignListResponse response = campaignReadService.getUpcomingCampaignsInMainPage(lang, category);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.MAIN_PAGE_UPCOMING_CAMPAIGNS_GET_SUCCESS.getMessage(), response);
    }


}
