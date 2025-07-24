package com.lokoko.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record VideoReviewProductDetailResponse(
        @Schema(requiredMode = REQUIRED)
        List<VideoReviewProductDetail> videoReviews
) {
}
