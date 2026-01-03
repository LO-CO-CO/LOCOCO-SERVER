package com.lokoko.domain.user.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ApproveCampaignIdsRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "승인할 캠페인 ID 목록", example = "[1, 2, 3]")
        List<Long> campaignIds
) {
}
