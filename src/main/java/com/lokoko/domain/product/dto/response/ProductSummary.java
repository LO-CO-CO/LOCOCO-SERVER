package com.lokoko.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record ProductSummary(
        @Schema(requiredMode = REQUIRED)
        String imageUrl,
        @Schema(requiredMode = REQUIRED)
        Long reviewCount,
        @Schema(requiredMode = REQUIRED)
        Double avgRating
) {
}
