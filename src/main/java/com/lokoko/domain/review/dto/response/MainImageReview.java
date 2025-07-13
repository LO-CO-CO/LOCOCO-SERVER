package com.lokoko.domain.review.dto.response;

public record MainImageReview(
        Long reviewId,
        String brandName,
        String productName,
        int likeCount,
        int rank,
        String reviewImage
) {
}


