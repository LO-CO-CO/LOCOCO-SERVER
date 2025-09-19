package com.lokoko.domain.brand.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record BrandMyCampaignInfoResponse(
        @Schema(requiredMode = REQUIRED, description = "캠페인 id", example = "1")
        Long campaignId,
        @Schema(requiredMode = REQUIRED, description = "캠페인 제목", example = "나는야 멋진 캠페인")
        String campaignTitle,
        @Schema(requiredMode = REQUIRED, description = "캠페인 시작일", example = "2025-09-16T00:21:04Z")
        Instant startDate,
        @Schema(requiredMode = REQUIRED, description = "캠페인 종료일", example = "2025-09-16T00:21:04Z")
        Instant endDate
) {
}
