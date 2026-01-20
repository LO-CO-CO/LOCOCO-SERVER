package com.lokoko.domain.productReview.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductAndReviewCountResponse(
        @Schema(description = "브랜드명 (null이면 전체 조회)")
        String brandName,
        @Schema(requiredMode = REQUIRED, description = "해당 브랜드의 상품 수")
        int productCount,
        @Schema(requiredMode = REQUIRED, description = "해당 브랜드의 전체 리뷰 수")
        int reviewCount
) {
    public static ProductAndReviewCountResponse of(String brandName, int productCount, int reviewCount) {
        return new ProductAndReviewCountResponse(brandName, productCount, reviewCount);
    }
}