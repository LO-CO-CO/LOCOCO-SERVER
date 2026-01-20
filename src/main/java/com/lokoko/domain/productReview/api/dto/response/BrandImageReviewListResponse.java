package com.lokoko.domain.productReview.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Slice;

public record BrandImageReviewListResponse(
        @Schema(requiredMode = REQUIRED)
        String brandName,
        @Schema(requiredMode = REQUIRED)
        List<ImageReviewResponse> reviews,
        @Schema(requiredMode = REQUIRED)
        PageableResponse pageInfo
) {
    public static BrandImageReviewListResponse from(String brandName, Slice<ImageReviewResponse> reviews) {
        return new BrandImageReviewListResponse(
                brandName,
                reviews.getContent(),
                PageableResponse.of(reviews)
        );
    }
}