package com.lokoko.domain.product.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record SimpleProductResponse(
        @Schema(requiredMode = REQUIRED)
        Long productId,
        @Schema(requiredMode = REQUIRED)
        String imageUrl,
        @Schema(requiredMode = REQUIRED)
        String productName,
        @Schema(requiredMode = REQUIRED)
        String brandName,
        @Schema(requiredMode = REQUIRED)
        String unit,
        @Schema(requiredMode = REQUIRED)
        Long reviewCount,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.0")
        @Schema(requiredMode = REQUIRED)
        Double avgRating
) {
    public SimpleProductResponse {
        reviewCount = reviewCount != null ? reviewCount : 0L;
        avgRating = avgRating != null ? Math.round(avgRating * 10) / 10.0 : 0.0;
    }
}