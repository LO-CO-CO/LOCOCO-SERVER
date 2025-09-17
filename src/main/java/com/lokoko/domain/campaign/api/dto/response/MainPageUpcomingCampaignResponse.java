package com.lokoko.domain.campaign.api.dto.response;

import com.lokoko.domain.campaign.domain.entity.enums.CampaignLanguage;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record MainPageUpcomingCampaignResponse(
        @Schema(requiredMode = REQUIRED, description = "캠페인 ID", example = "1")
        Long campaignId,
        @Schema(requiredMode = REQUIRED, description = "캠페인 타입", example = "GIVEAWAY")
        CampaignType campaignType,
        @Schema(requiredMode = REQUIRED, description = "캠페인 언어", example = "ENG")
        CampaignLanguage language,
        @Schema(requiredMode = REQUIRED, description = "브랜드명", example = "Anua")
        String brandName,
        @Schema(requiredMode = REQUIRED, description = "캠페인 이미지 URL", example = "https://example.com/image.jpg")
        String campaignImageUrl,
        @Schema(requiredMode = REQUIRED, description = "캠페인명", example = "Anua campaign")
        String campaignName,
        @Schema(requiredMode = REQUIRED, description = "지원자 수", example = "10")
        Integer applicantNumber,
        @Schema(requiredMode = REQUIRED, description = "모집 인원", example = "10")
        Integer recruitmentNumber,
        @Schema(requiredMode = REQUIRED, description = "시작 시간", example = "2024-12-31T10:00:00Z")
        Instant startTime,
        @Schema(requiredMode = REQUIRED, description = "칩 상태", example = "disabled")
        String chipStatus
) {
}
