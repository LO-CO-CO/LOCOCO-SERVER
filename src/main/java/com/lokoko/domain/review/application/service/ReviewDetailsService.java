package com.lokoko.domain.review.application.service;

import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.entity.ReceiptImage;
import com.lokoko.domain.image.entity.ReviewImage;
import com.lokoko.domain.image.repository.ProductImageRepository;
import com.lokoko.domain.image.repository.ReceiptImageRepository;
import com.lokoko.domain.image.repository.ReviewImageRepository;
import com.lokoko.domain.like.domain.repository.ReviewLikeRepository;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.review.api.dto.response.ImageReviewDetailResponse;
import com.lokoko.domain.review.api.dto.response.VideoReviewDetailResponse;
import com.lokoko.domain.review.domain.entity.Review;
import com.lokoko.domain.review.exception.ProductImageNotFoundException;
import com.lokoko.domain.review.exception.ReviewNotFoundException;
import com.lokoko.domain.review.exception.ReviewVideoNotFoundException;
import com.lokoko.domain.review.domain.repository.ReviewRepository;
import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.entity.enums.Role;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.domain.user.repository.UserRepository;
import com.lokoko.domain.video.domain.entity.ReviewVideo;
import com.lokoko.domain.video.domain.repository.ReviewVideoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewDetailsService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewVideoRepository reviewVideoRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReceiptImageRepository receiptImageRepository;
    private final ProductImageRepository productImageRepository;


    public VideoReviewDetailResponse getVideoReviewDetails(Long reviewId, Long userId) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(UserNotFoundException::new);
        }
        Review review = getReview(reviewId);
        List<ReviewVideo> reviewVideos = reviewVideoRepository.findAllByReviewId(reviewId);
        long totalLikes = reviewLikeRepository.countByReviewId(reviewId);
        ReceiptImage receiptImage = null;
        if (user != null && user.getRole() == Role.ADMIN) {
            receiptImage = getReceiptImageIfAdmin(user, reviewId);
        }
        String receiptImageUrl = (receiptImage != null)
                ? receiptImage.getMediaFile().getFileUrl()
                : null;
        boolean isLiked = (user != null)
                && reviewLikeRepository.existsByUserAndReview(user, review);
        Product product = review.getProduct();
        ProductImage productImage = productImageRepository
                .findByProductAndIsMainTrue(product)
                .orElseThrow(ProductImageNotFoundException::new);

        return VideoReviewDetailResponse.from(
                review,
                reviewVideos,
                totalLikes,
                receiptImageUrl,
                productImage,
                isLiked
        );
    }


    public ImageReviewDetailResponse getImageReviewDetails(Long reviewId, Long userId) {
        Review review = getReview(reviewId);
        List<ReviewImage> reviewImages = reviewImageRepository.findByReviewId(reviewId);
        long totalLikes = reviewLikeRepository.countByReviewId(reviewId);

        User user = (userId != null)
                ? userRepository.findById(userId).orElseThrow(UserNotFoundException::new)
                : null;

        ReceiptImage receipt = (user != null && user.getRole() == Role.ADMIN)
                ? getReceiptImageIfAdmin(user, reviewId)
                : null;

        String receiptImageUrl = (receipt != null)
                ? receipt.getMediaFile().getFileUrl()
                : null;

        boolean isLiked = false;
        if (user != null) {
            isLiked = reviewLikeRepository.existsByUserAndReview(user, review);
        }

        Product product = review.getProduct();
        ProductImage productImage = productImageRepository
                .findByProductAndIsMainTrue(product)
                .orElseThrow(ProductImageNotFoundException::new);

        return ImageReviewDetailResponse.from(review, reviewImages, totalLikes,
                receiptImageUrl, productImage, isLiked);
    }

    private Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);
    }

    private ReviewVideo getReviewVideo(Long reviewId) {
        return reviewVideoRepository.findByReviewId(reviewId)
                .orElseThrow(ReviewVideoNotFoundException::new);
    }

    private ReceiptImage getReceiptImageIfAdmin(User user, Long reviewId) {
        return user.getRole() == Role.ADMIN
                ? receiptImageRepository.findByReviewId(reviewId).orElse(null)
                : null;
    }
}
