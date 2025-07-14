package com.lokoko.domain.product.dto;

import com.lokoko.domain.product.dto.response.ProductSummary;
import com.lokoko.domain.product.entity.Product;
import java.util.Arrays;
import java.util.Optional;

public record ProductMainImageResponse(
        Long productId,                // 제품 ID
        Optional<String> url,         // 대표 이미지 (1개만, Optional)
        String productName,           // 제품명
        String brandName,             // 브랜드명
        String unit,                  // 용량/단위
        Long reviewCount,             // 리뷰 수
        Double rating,                // 평균 별점
        Boolean isLiked               // 사용자 좋아요 여부
) {
    public static ProductMainImageResponse of(
            Product product,
            ProductSummary summary,
            boolean isLiked
    ) {
        Optional<String> url = Optional.ofNullable(summary.imageUrl())
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(u -> u.contains(",")
                        ? Arrays.stream(u.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .findFirst()
                        .orElse(null)
                        : u
                )
                .map(Optional::ofNullable)
                .orElse(Optional.empty());

        return new ProductMainImageResponse(
                product.getId(),
                url,
                product.getProductName(),
                product.getBrandName(),
                product.getUnit(),
                summary.reviewCount(),
                summary.avgRating(),
                isLiked
        );
    }
}
