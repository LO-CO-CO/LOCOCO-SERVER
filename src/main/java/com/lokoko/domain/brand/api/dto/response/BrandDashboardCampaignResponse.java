package com.lokoko.domain.brand.api.dto.response;

import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.*;


public record BrandDashboardCampaignResponse(
        @Schema(requiredMode = REQUIRED, description = "캠페인 ID", example = "1")
        Long campaignId,
        @Schema(requiredMode = REQUIRED, description = "캠페인 썸네일 이미지 URL")
        String thumbnailUrl,
        @Schema(requiredMode = REQUIRED, description = "캠페인 제목", example = "Glow Serum Launch")
        String title,
        @Schema(requiredMode = REQUIRED, description = "캠페인 시작일", example = "2026-12-28T00:00:00Z")
        Instant startDate,
        @Schema(requiredMode = REQUIRED, description = "캠페인 종료일", example = "2026-12-28T23:59:59Z")
        Instant endDate,
        @Schema(requiredMode = REQUIRED, description = "캠페인 상태", example = "RECRUITING")
        CampaignStatus status,
        @Schema(requiredMode = REQUIRED, description = "참여 크리에이터 수", example = "100")
        Integer participantCreatorCount,
        @Schema(requiredMode = REQUIRED, description = "인스타그램 포스트 수", example = "22")
        Long instaPostCount,
        @Schema(requiredMode = REQUIRED, description = "인스타그램 릴스 수", example = "22")
        Long instaReelsCount,
        @Schema(requiredMode = REQUIRED, description = "틱톡 비디오 수", example = "22")
        Long tiktokVideoCount
) {}