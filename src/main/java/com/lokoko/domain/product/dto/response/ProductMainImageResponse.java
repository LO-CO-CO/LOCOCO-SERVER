package com.lokoko.domain.product.dto.response;

import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.exception.MissingProductImageException;
import java.util.Arrays;

public record ProductMainImageResponse(
        Long productId,     // 제품 ID
        String url,         // 대표 이미지 (반드시 존재해야 함)
        String productName, // 제품명
        String brandName,   // 브랜드명
        String unit,        // 용량/단위
        Long reviewCount,   // 리뷰 수
        Double rating,      // 평균 별점
        Boolean isLiked     // 사용자 좋아요 여부
) {
    public static ProductMainImageResponse of(
            Product product,
            ProductSummary summary,
            boolean isLiked
    ) {
        String imageUrl = extractMainImage(summary.imageUrl());

        return new ProductMainImageResponse(
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
