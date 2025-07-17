package com.lokoko.domain.product.dto.response;

import java.util.List;

public record PopularProductProjection(
        Long productId,
        String productName,
        String brandName,
        String unit,
        Long reviewCount,
        Double avgRating,
        String imageUrl,
        Boolean isLiked
) {
    // Compact constructor로 데이터 정규화
    public PopularProductProjection {
        reviewCount = reviewCount != null ? reviewCount : 0L;
        avgRating = avgRating != null ? Math.round(avgRating * 10) / 10.0 : 0.0;
        isLiked = isLiked != null ? isLiked : false;
    }

    public ProductResponse toProductResponse() {
        return ProductResponse.builder()
                .productId(productId)
                .imageUrls(imageUrl != null ? List.of(imageUrl) : List.of())
                .productName(productName)
                .brandName(brandName)
                .unit(unit)
                .reviewCount(reviewCount)
                .rating(avgRating)
                .isLiked(isLiked)
                .build();
    }

    public ProductResponse toProductResponseWithLike(boolean userIsLiked) {
        return ProductResponse.builder()
                .productId(productId)
                .imageUrls(imageUrl != null ? List.of(imageUrl) : List.of())
                .productName(productName)
                .brandName(brandName)
                .unit(unit)
                .reviewCount(reviewCount)
                .rating(avgRating)
                .isLiked(userIsLiked)
                .build();
    }
}
