package com.lokoko.domain.product.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductStatsResponse(
        @Schema(requiredMode = REQUIRED)
        String imageUrl,
        @Schema(requiredMode = REQUIRED)
        Long reviewCount,
        @Schema(requiredMode = REQUIRED)
        Double avgRating
) {
}
