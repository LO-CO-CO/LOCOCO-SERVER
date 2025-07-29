package com.lokoko.domain.review.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.video.domain.entity.ReviewVideo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

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
        List<String> videoUrls,
        String profileImageUrl,
        @Schema(requiredMode = REQUIRED)
        String authorName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.0")
        @Schema(requiredMode = REQUIRED)
        Double rating,
        String option,
        @Schema(requiredMode = REQUIRED)
        LocalDateTime uploadAt,
        @Schema(requiredMode = REQUIRED)
        String productImageUrl,
        String receiptImageUrl,
        @Schema(requiredMode = REQUIRED)
        Boolean receiptUploaded,
        @Schema(requiredMode = REQUIRED)
        Boolean isLiked,
        @Schema(requiredMode = REQUIRED)
        Long productId
) {
    public static VideoReviewDetailResponse from(Review review, List<ReviewVideo> reviewVideos, long likeCount,
                                                 String receiptImageUrl, ProductImage productImage,
                                                 Boolean isLiked) {
        List<String> videoUrls = reviewVideos.stream()
                .map(rv -> rv.getMediaFile().getFileUrl())
                .toList();

        LocalDateTime uploadAt = reviewVideos.stream()
                .map(ReviewVideo::getCreatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(review.getCreatedAt());

        String optionName = review.getProductOption() != null
                ? review.getProductOption().getOptionName()
                : null;

        return new VideoReviewDetailResponse(
                review.getId(),
                review.getProduct().getBrandName(),
                review.getProduct().getProductName(),
                review.getPositiveContent(),
                review.getNegativeContent(),
                likeCount,
                videoUrls,
                review.getAuthor().getProfileImageUrl(),
                review.getAuthor().getNickname(),
                (double) review.getRating().getValue(),
                optionName,
                uploadAt,
                productImage.getUrl(),
                receiptImageUrl,
                review.isReceiptUploaded(),
                isLiked,
                review.getProduct().getId()
        );
    }
}
