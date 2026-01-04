package com.lokoko.domain.user.api.dto.response;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record AdminCampaignListResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "캠페인 목록")
        List<AdminCampaignInfoResponse> campaigns,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "페이징 정보")
        PageableResponse pageInfo
) {
}
