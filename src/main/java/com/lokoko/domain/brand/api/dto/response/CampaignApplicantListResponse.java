package com.lokoko.domain.brand.api.dto.response;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record CampaignApplicantListResponse(
        @Schema(requiredMode = REQUIRED, description = "캠페인에 지원한 크리에이터 목록")
        List<CampaignApplicantResponse> applicants,
        @Schema(requiredMode = REQUIRED, description = "페이징 정보")
        PageableResponse pageInfo
) {
}
