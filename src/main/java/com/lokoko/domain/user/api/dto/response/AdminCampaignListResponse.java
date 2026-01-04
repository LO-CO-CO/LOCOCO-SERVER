package com.lokoko.domain.user.api.dto.response;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record AdminCampaignListResponse(
        @Schema(requiredMode = REQUIRED, description = "캠페인 목록")
        List<AdminCampaignInfoResponse> campaigns,
        @Schema(requiredMode = REQUIRED, description = "총 캠페인 개수")
        Long totalCampaignCount,
        @Schema(requiredMode = REQUIRED, description = "페이징 정보")
        PageableResponse pageInfo
) {
}
