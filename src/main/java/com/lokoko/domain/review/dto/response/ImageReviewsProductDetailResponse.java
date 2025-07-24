package com.lokoko.domain.review.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ImageReviewsProductDetailResponse(
        @Schema(requiredMode = REQUIRED)
        Boolean isAdmin,
        @Schema(requiredMode = REQUIRED)
        List<ImageReviewProductDetailResponse> imageReviews,
        @Schema(requiredMode = REQUIRED)
        PageableResponse pageInfo
) {
}