package com.lokoko.domain.brand.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record CreatorApprovedResponse(
        @Schema(requiredMode = REQUIRED, description = "현재 승인된 크리에이터 수", example = "1")
        Integer currentApprovedNumber,
        @Schema(requiredMode = REQUIRED, description = "총 모집 인원", example = "10")
        Integer recruitmentNumber
) {
}
