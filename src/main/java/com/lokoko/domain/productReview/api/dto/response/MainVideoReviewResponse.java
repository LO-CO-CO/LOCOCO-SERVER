package com.lokoko.domain.productReview.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;


public record MainVideoReviewResponse(
        @Schema(requiredMode = REQUIRED)
        List<MainVideoReview> videoReviews
) {
}
