package com.lokoko.domain.productReview.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewResponse(
        @Schema(requiredMode = REQUIRED)
        Long reviewId
) {
}
