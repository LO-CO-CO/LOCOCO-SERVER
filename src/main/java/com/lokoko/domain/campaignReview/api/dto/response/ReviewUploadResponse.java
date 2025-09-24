package com.lokoko.domain.campaignReview.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReviewUploadResponse(

        @Schema(requiredMode = REQUIRED, description = "생성된 리뷰 ID", example = "1")
        Long reviewId
) {
}
