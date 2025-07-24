package com.lokoko.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record MainVideoReview(
        @Schema(requiredMode = REQUIRED)
        Long reviewId,
        @Schema(requiredMode = REQUIRED)
        Long productId,
        @Schema(requiredMode = REQUIRED)
        String brandName,
        @Schema(requiredMode = REQUIRED)
        String productName,
        @Schema(requiredMode = REQUIRED)
        Integer likeCount,
        @Schema(requiredMode = REQUIRED)
        Integer rank,
        @Schema(requiredMode = REQUIRED)
        String reviewVideo
) {
}

