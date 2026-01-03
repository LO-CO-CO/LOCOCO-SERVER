package com.lokoko.domain.user.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record DeleteCampaignIdsRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "삭제할 캠페인 ID 목록", example = "[1, 2, 3]")
        List<Long> campaignIds
) {
}
