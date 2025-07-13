package com.lokoko.domain.review.dto.response;

public record MainVideoReview(
        Long reviewId,
        String brandName,
        String productName,
        int likeCount,
        int rank,
        String reviewVideo
) {
}

