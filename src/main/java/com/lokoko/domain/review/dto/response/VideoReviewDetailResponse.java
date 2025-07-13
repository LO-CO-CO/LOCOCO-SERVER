package com.lokoko.domain.review.dto.response;

import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.video.entity.ReviewVideo;
import java.time.LocalDateTime;

public record VideoReviewDetailResponse(
        Long reviewId,
        String brandName,
        String productName,
        String positiveContent,
        String negativeContent,
        long likeCount,
        String videoUrl,
        String profileImageUrl,
        String authorName,
        String rating,
        LocalDateTime uploadAt
) {
    public static VideoReviewDetailResponse from(ReviewVideo reviewVideo, long likeCount) {
        Review review = reviewVideo.getReview();
        User author = review.getAuthor();
        LocalDateTime uploadAt = reviewVideo.getCreatedAt();

        return new VideoReviewDetailResponse(
                review.getId(),
                review.getProduct().getBrandName(),
                review.getProduct().getProductName(),
                review.getPositiveContent(),
                review.getNegativeContent(),
                likeCount,
                reviewVideo.getMediaFile().getFileUrl(),
                author.getProfileImageUrl(),
                author.getNickname(),
                review.getRating().name(),
                uploadAt
        );
    }
}
