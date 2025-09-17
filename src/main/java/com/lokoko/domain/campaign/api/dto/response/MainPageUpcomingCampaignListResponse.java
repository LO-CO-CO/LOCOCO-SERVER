package com.lokoko.domain.campaign.api.dto.response;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record MainPageUpcomingCampaignListResponse(
        @Schema(requiredMode = REQUIRED, description = "메인 페이지 오픈 예정 캠페인 목록")
        List<MainPageUpcomingCampaignResponse> campaigns,
        @Schema(requiredMode = REQUIRED, description = "페이지 정보")
        PageableResponse pageInfo
) {
}
