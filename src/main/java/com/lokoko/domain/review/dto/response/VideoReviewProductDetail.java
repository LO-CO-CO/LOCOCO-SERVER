package com.lokoko.domain.review.dto.response;

import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.video.entity.ReviewVideo;

public record VideoReviewProductDetail(
        Long reviewId,
        String brandName,
        String productName,
        int likeCount,
        String videoUrl
) {

    public static VideoReviewProductDetail from(ReviewVideo reviewVideo) {
        Review review = reviewVideo.getReview();
        return new VideoReviewProductDetail(
                review.getId(),
                review.getProduct().getBrandName(),
                review.getProduct().getProductName(),
                review.getLikeCount(),
                reviewVideo.getMediaFile().getFileUrl()
        );
    }
}

