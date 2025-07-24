package com.lokoko.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record ScorePercent(
        @Schema(requiredMode = REQUIRED)
        int score,
        @Schema(requiredMode = REQUIRED)
        double percent
) {
}
