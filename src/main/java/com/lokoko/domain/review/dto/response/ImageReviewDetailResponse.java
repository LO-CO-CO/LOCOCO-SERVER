package com.lokoko.domain.review.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.entity.ReviewImage;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

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
        String profileImageUrl,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.0")
        @Schema(requiredMode = REQUIRED)
        Double rating,
        String option,
        @Schema(requiredMode = REQUIRED)
        Long likeCount,
        @Schema(requiredMode = REQUIRED)
        List<String> images,
        @Schema(requiredMode = REQUIRED)
        String brandName,
        @Schema(requiredMode = REQUIRED)
        String productName,
        @Schema(requiredMode = REQUIRED)
        String productImageUrl,
        String receiptImageUrl,
        @Schema(requiredMode = REQUIRED)
        Boolean isLiked,
        @Schema(requiredMode = REQUIRED)
        Long productId

) {
    public static ImageReviewDetailResponse from(Review review, List<ReviewImage> reviewImages,
                                                 long totalLikes, String receiptImage, ProductImage productImage,
                                                 Boolean isLiked) {
        Product product = review.getProduct();
        User author = review.getAuthor();

        List<String> images = reviewImages.stream()
                .map(reviewImage -> reviewImage.getMediaFile().getFileUrl())
                .toList();

        String optionName = review.getProductOption() != null
                ? review.getProductOption().getOptionName()
                : null;

        return new ImageReviewDetailResponse(
                review.getId(),
                review.getModifiedAt(),
                review.isReceiptUploaded(),
                review.getPositiveContent(),
                review.getNegativeContent(),
                author.getNickname(),
                author.getProfileImageUrl(),
                (double) review.getRating().getValue(),
                optionName,
                totalLikes,
                images,
                product.getBrandName(),
                product.getProductName(),
                productImage.getUrl(),
                receiptImage,
                isLiked,
                product.getId()
        );
    }
}
