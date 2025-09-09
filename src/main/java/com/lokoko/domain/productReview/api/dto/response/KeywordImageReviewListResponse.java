package com.lokoko.domain.productReview.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Slice;

public record KeywordImageReviewListResponse(
        @Schema(requiredMode = REQUIRED)
        String searchQuery,
        @Schema(requiredMode = REQUIRED)
        List<ImageReviewResponse> reviews,
        @Schema(requiredMode = REQUIRED)
        PageableResponse pageInfo
) {
    public static KeywordImageReviewListResponse from(String keyword, Slice<ImageReviewResponse> reviews) {
        return new KeywordImageReviewListResponse(
                keyword,
                reviews.getContent(),
                PageableResponse.of(reviews)
        );
    }
}
