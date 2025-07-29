package com.lokoko.domain.product.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.exception.MissingProductImageException;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

public record ProductListItemResponse(
        @Schema(requiredMode = REQUIRED)
        Long productId,     // 제품 ID
        @Schema(requiredMode = REQUIRED)
        String url,         // 대표 이미지 (반드시 존재해야 함)
        @Schema(requiredMode = REQUIRED)
        String productName, // 제품명
        @Schema(requiredMode = REQUIRED)
        String brandName,   // 브랜드명
        @Schema(requiredMode = REQUIRED)
        String unit,        // 용량/단위
        @Schema(requiredMode = REQUIRED)
        Long reviewCount,   // 리뷰 수
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.0")
        @Schema(requiredMode = REQUIRED)
        Double rating,      // 평균 별점
        @Schema(requiredMode = REQUIRED)
        Boolean isLiked     // 사용자 좋아요 여부
) {
    public static ProductListItemResponse of(
            Product product,
            ProductStatsResponse summary,
            boolean isLiked
    ) {
        String imageUrl = extractMainImage(summary.imageUrl());

        return new ProductListItemResponse(
                product.getId(),
                imageUrl,
                product.getProductName(),
                product.getBrandName(),
                product.getUnit(),
                summary.reviewCount(),
                summary.avgRating(),
                isLiked
        );
    }

    private static String extractMainImage(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new MissingProductImageException();
        }
        return raw.contains(",")
                ? Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .findFirst()
                .orElseThrow(MissingProductImageException::new)
                : raw.trim();
    }
}
