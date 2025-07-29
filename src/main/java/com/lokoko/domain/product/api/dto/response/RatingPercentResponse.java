package com.lokoko.domain.product.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;

public record RatingPercentResponse(
        @Schema(requiredMode = REQUIRED)
        int score,
        @Schema(requiredMode = REQUIRED)
        double percent
) {
}
