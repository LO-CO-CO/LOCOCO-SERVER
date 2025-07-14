package com.lokoko.domain.review.dto.response;

import com.lokoko.domain.image.entity.ReviewImage;
import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;

public record ImageReviewDetailResponse(
        Long reviewId,
        LocalDateTime writtenTime,
        Boolean receiptUploaded,
        String positiveComment,
        String negativeComment,
        String authorName,
        String profileImageUrl,
        String rating,
        String option,
        long likeCount,
        List<String> images,
        String brandName,
        String productName
) {
    public static ImageReviewDetailResponse from(User author, Review review, List<ReviewImage> reviewImages,
                                                 long totalLikes) {
        Product product = review.getProduct();

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
                product.getProductName()
        );
    }
}
