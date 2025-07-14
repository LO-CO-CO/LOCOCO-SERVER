package com.lokoko.domain.review.dto.response;

import com.lokoko.domain.image.entity.ReceiptImage;
import com.lokoko.domain.image.entity.ReviewImage;
import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record ImageReviewDetailResponse(
        @Schema(requiredMode = REQUIRED)
        Long reviewId,
        @Schema(requiredMode = REQUIRED)
        LocalDateTime writtenTime,
        @Schema(requiredMode = REQUIRED)
        Boolean receiptUploaded,
        @Schema(requiredMode = REQUIRED)
        String positiveComment,
        @Schema(requiredMode = REQUIRED)
        String negativeComment,
        @Schema(requiredMode = REQUIRED)
        String authorName,
        @Schema(requiredMode = REQUIRED)
        String profileImageUrl,
        @Schema(requiredMode = REQUIRED)
        String rating,
        @Schema(requiredMode = REQUIRED)
        String option,
        @Schema(requiredMode = REQUIRED)
        Long likeCount,
        @Schema(requiredMode = REQUIRED)
        List<String> images,
        @Schema(requiredMode = REQUIRED)
        String brandName,
        @Schema(requiredMode = REQUIRED)
        String productName,
        String receiptImageUrl
) {
    public static ImageReviewDetailResponse from(Review review, List<ReviewImage> reviewImages,
                                                 long totalLikes, ReceiptImage receiptImage, Role requestUserRole) {
        Product product = review.getProduct();
        User author = review.getAuthor();

        List<String> images = reviewImages.stream()
                .map(reviewImage -> reviewImage.getMediaFile().getFileUrl())
                .toList();

        return new ImageReviewDetailResponse(
                review.getId(),
                review.getModifiedAt(),
                review.isReceiptUploaded(),
                review.getPositiveContent(),
                review.getNegativeContent(),
                author.getNickname(),
                author.getProfileImageUrl(),
                review.getRating().name(),
                review.getProductOption().getOptionName(),
                totalLikes,
                images,
                product.getBrandName(),
                product.getProductName(),
                requestUserRole == Role.ADMIN && receiptImage != null ?
                        receiptImage.getMediaFile().getFileUrl() : null
        );
    }
}
