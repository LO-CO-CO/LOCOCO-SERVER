package com.lokoko.domain.review.dto.response;

public record VideoReviewProductDetail(
        Long reviewId,
        String brandName,
        String productName,
        int likeCount,
        String videoUrl
) {
}

