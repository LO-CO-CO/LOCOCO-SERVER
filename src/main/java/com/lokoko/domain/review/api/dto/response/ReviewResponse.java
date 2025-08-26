package com.lokoko.domain.review.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record ReviewResponse(
        @Schema(requiredMode = REQUIRED)
        Long reviewId
) {
}
