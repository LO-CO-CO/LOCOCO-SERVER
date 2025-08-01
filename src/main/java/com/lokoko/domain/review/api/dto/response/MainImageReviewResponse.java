package com.lokoko.domain.review.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record MainImageReviewResponse(
        @Schema(requiredMode = REQUIRED)
        List<MainImageReview> imageReviews
) {
}
