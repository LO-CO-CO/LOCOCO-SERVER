package com.lokoko.domain.user.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record AdminCampaignInfoResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "캠페인 ID", example = "1")
        Long campaignId,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "브랜드명", example = "로코코")
        String brandName,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "캠페인명", example = "신제품 체험단")
        String campaignName,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "모집 현황")
        RecruitmentStatus recruitmentStatus,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "신청 시작일", example = "2024-12-01T00:00:00Z")
        Instant applyStartDate,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "신청 마감일", example = "2024-12-15T23:59:59Z")
        Instant applyDeadline,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "승인 상태", example = "PENDING 또는 APPROVED")
        String approvedStatus
) {

    public record RecruitmentStatus(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "모집 인원", example = "10")
            int recruitmentNumber,

            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "신청자 수", example = "5")
            int applicantNumber
    ) {}
}
