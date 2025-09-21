package com.lokoko.domain.brand.api.dto.response;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.*;

public record BrandMyCampaignListResponse(
        @Schema(requiredMode = REQUIRED, description = "브랜드 마이페이지 캠페인 리스트")
        List<BrandMyCampaignResponse> campaigns,
        @Schema(requiredMode = REQUIRED, description = "페이징 정보")
        PageableResponse pageInfo
) {
}
