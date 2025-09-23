package com.lokoko.domain.brand.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record BrandMyCampaignResponse(
        @Schema(requiredMode = REQUIRED, description = "캠페인 id", example = "1")
        Long id,
        @Schema(requiredMode = REQUIRED, description = "캠페인 대표 이미지 url")
        String campaignImageUrl,
        @Schema(requiredMode = REQUIRED, description = "캠페인 제목", example = "1")
        String title,
        @Schema(requiredMode = REQUIRED, description = "캠페인 마감기간", example = "2025-09-17T07:32:08.995Z")
        Instant applyDeadline,
        @Schema(requiredMode = REQUIRED, description = "캠페인 지원자 수", example = "1")
        Integer applicantNumber,
        @Schema(requiredMode = REQUIRED, description = "캠페인 모집인원 수", example = "10")
        Integer recruitmentNumber,
        @Schema(requiredMode = REQUIRED, description = "캠페인 상태", example = "DRAFT")
        String campaignStatus
) {
}
