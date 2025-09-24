package com.lokoko.domain.brand.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.*;

public record BrandMyCampaignInfoListResponse(
        @Schema(requiredMode = REQUIRED, description = "캠페인 정보 리스트")
        List<BrandMyCampaignInfoResponse> campaignInfos
) {
}
