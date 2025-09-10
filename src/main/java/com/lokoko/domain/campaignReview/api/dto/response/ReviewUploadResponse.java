package com.lokoko.domain.campaignReview.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReviewUploadResponse(

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "생성된 리뷰 ID", example = "1")
        Long reviewId
) {
}
