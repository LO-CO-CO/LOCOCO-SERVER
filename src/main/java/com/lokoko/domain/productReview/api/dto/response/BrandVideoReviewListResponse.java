package com.lokoko.domain.productReview.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Slice;

@Builder
public record BrandVideoReviewListResponse(
        @Schema(requiredMode = REQUIRED)
        String brandName,
        @Schema(requiredMode = REQUIRED)
        List<VideoReviewResponse> reviews,
        @Schema(requiredMode = REQUIRED)
        PageableResponse pageInfo
) {
    public static BrandVideoReviewListResponse from(String brandName, Slice<VideoReviewResponse> reviews) {
        return new BrandVideoReviewListResponse(
                brandName,
                reviews.getContent(),
                PageableResponse.of(reviews)
        );
    }
}
