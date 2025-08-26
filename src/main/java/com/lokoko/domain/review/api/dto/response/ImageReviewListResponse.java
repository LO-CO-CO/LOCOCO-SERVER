package com.lokoko.domain.review.api.dto.response;

import com.lokoko.global.common.response.PageableResponse;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
public record ImageReviewListResponse(
        @Schema(requiredMode = REQUIRED)
        String searchQuery,
        @Schema(requiredMode = REQUIRED)
        String parentCategoryName,
        @Schema(requiredMode = REQUIRED)
        List<ImageReviewResponse> reviews,
        @Schema(requiredMode = REQUIRED)
        PageableResponse pageInfo
) {
}
