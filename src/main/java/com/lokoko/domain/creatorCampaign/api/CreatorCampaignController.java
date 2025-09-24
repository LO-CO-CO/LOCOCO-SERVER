package com.lokoko.domain.creatorCampaign.api;

import com.lokoko.domain.creatorCampaign.api.message.ResponseMessage;
import com.lokoko.domain.creatorCampaign.application.usecase.CreatorCampaignUsecase;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CREATOR CAMPAIGN")
@RestController
@RequestMapping("/api/creator-campaign")
@RequiredArgsConstructor
public class CreatorCampaignController {

    private final CreatorCampaignUsecase creatorCampaignUsecase;

    @Operation(summary = "캠페인 참여 신청하기")
    @PostMapping("/{campaignId}/participate")
    public ApiResponse<Void> participate(@PathVariable Long campaignId,
                                         @Parameter(hidden = true) @CurrentUser Long userId) {
        creatorCampaignUsecase.participateCampaign(userId, campaignId);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PARTICIPATE_CAMPAIGN_SUCCESS.getMessage(), null);
    }
}
