package com.lokoko.domain.campaign.api;

import com.lokoko.domain.campaign.api.dto.request.CampaignMediaRequest;
import com.lokoko.domain.campaign.api.dto.response.CampaignDetailResponse;
import com.lokoko.domain.campaign.api.dto.response.CampaignMediaResponse;
import com.lokoko.domain.campaign.api.message.ResponseMessage;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.application.service.CampaignService;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
