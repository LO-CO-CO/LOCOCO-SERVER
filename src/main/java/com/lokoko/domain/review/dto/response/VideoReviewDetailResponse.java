package com.lokoko.domain.review.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.video.entity.ReviewVideo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

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
        String profileImageUrl,
        @Schema(requiredMode = REQUIRED)
        String authorName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.0")
        @Schema(requiredMode = REQUIRED)
        Double rating,
        @Schema(requiredMode = REQUIRED)
        LocalDateTime uploadAt,
        @Schema(requiredMode = REQUIRED)
        String productImageUrl,
        String receiptImageUrl,
        @Schema(requiredMode = REQUIRED)
        Boolean isLiked,
        @Schema(requiredMode = REQUIRED)
        Long productId
) {
    public static VideoReviewDetailResponse from(ReviewVideo reviewVideo, long likeCount,
                                                 String receiptImageUrl, ProductImage productImage,
                                                 Boolean isLiked) {
        Review review = reviewVideo.getReview();
        Product product = review.getProduct();
        User author = review.getAuthor();
        LocalDateTime uploadAt = reviewVideo.getCreatedAt();

        return new VideoReviewDetailResponse(
                review.getId(),
                product.getBrandName(),
                product.getProductName(),
                review.getPositiveContent(),
                review.getNegativeContent(),
                likeCount,
                reviewVideo.getMediaFile().getFileUrl(),
                author.getProfileImageUrl(),
                author.getNickname(),
                (double) review.getRating().getValue(),
                uploadAt,
                productImage.getUrl(),
                receiptImageUrl,
                isLiked,
                product.getId()
        );
    }
}
