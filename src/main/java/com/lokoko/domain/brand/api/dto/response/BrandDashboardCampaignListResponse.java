package com.lokoko.domain.brand.api.dto.response;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record BrandDashboardCampaignListResponse(
        @Schema(requiredMode = REQUIRED, description = "브랜드 대시보드 캠페인 리스트")
        List<BrandDashboardCampaignResponse> campaigns,
        @Schema(requiredMode = REQUIRED, description = "페이징 정보")
        PageableResponse pageInfo
) {
}
