package com.lokoko.domain.review.dto.response;

import com.lokoko.domain.image.entity.ReceiptImage;
import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.entity.enums.Role;
import com.lokoko.domain.video.entity.ReviewVideo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record VideoReviewDetailResponse(
        @Schema(requiredMode = REQUIRED)
        Long reviewId,
        @Schema(requiredMode = REQUIRED)
        String brandName,
        @Schema(requiredMode = REQUIRED)
        String productName,
        @Schema(requiredMode = REQUIRED)
        String positiveContent,
        @Schema(requiredMode = REQUIRED)
        String negativeContent,
        @Schema(requiredMode = REQUIRED)
        Long likeCount,
        @Schema(requiredMode = REQUIRED)
        String videoUrl,
        @Schema(requiredMode = REQUIRED)
        String profileImageUrl,
        @Schema(requiredMode = REQUIRED)
        String authorName,
        @Schema(requiredMode = REQUIRED)
        String rating,
        @Schema(requiredMode = REQUIRED)
        LocalDateTime uploadAt,
        String receiptImageUrl
) {
    public static VideoReviewDetailResponse from(ReviewVideo reviewVideo, long likeCount,
                                                 ReceiptImage receiptImage, Role requestUserRole) {
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
                uploadAt,
                requestUserRole == Role.ADMIN && receiptImage != null ?
                        receiptImage.getMediaFile().getFileUrl() : null

        );
    }
}
