package com.lokoko.domain.productReview.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record VideoReviewListResponse(
        @Schema(requiredMode = REQUIRED)
        String searchQuery,
        @Schema(requiredMode = REQUIRED)
        String parentCategoryName,
        @Schema(requiredMode = REQUIRED)
        List<VideoReviewResponse> reviews,
        @Schema(requiredMode = REQUIRED)
        PageableResponse pageInfo
) {
}
