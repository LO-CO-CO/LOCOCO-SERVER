package com.lokoko.domain.productReview.application.service;

import com.lokoko.domain.like.domain.repository.ReviewLikeRepository;
import com.lokoko.domain.media.image.domain.entity.ProductImage;
import com.lokoko.domain.media.image.domain.entity.ReviewImage;
import com.lokoko.domain.media.image.domain.repository.ProductImageRepository;
import com.lokoko.domain.media.image.domain.repository.ReviewImageRepository;
import com.lokoko.domain.media.video.domain.entity.ReviewVideo;
import com.lokoko.domain.media.video.domain.repository.ReviewVideoRepository;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.productReview.api.dto.response.ImageReviewDetailResponse;
import com.lokoko.domain.productReview.api.dto.response.VideoReviewDetailResponse;
import com.lokoko.domain.productReview.domain.entity.Review;
import com.lokoko.domain.productReview.domain.repository.ReviewRepository;
import com.lokoko.domain.productReview.exception.ProductImageNotFoundException;
import com.lokoko.domain.productReview.exception.ReviewNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewDetailsService {

    private final ReviewRepository reviewRepository;
    private final ReviewVideoRepository reviewVideoRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ProductImageRepository productImageRepository;


    public VideoReviewDetailResponse getVideoReviewDetails(Long reviewId) {

        Review review = getReview(reviewId);
        List<ReviewVideo> reviewVideos = reviewVideoRepository.findAllByReviewId(reviewId);
        long totalLikes = reviewLikeRepository.countByReviewId(reviewId);

        Product product = review.getProduct();
        ProductImage productImage = productImageRepository
                .findByProductAndIsMainTrue(product)
                .orElseThrow(ProductImageNotFoundException::new);

        return VideoReviewDetailResponse.from(
                review,
                reviewVideos,
                totalLikes,
                productImage
        );
    }


    public ImageReviewDetailResponse getImageReviewDetails(Long reviewId) {
        Review review = getReview(reviewId);
        List<ReviewImage> reviewImages = reviewImageRepository.findByReviewId(reviewId);

        long totalLikes = reviewLikeRepository.countByReviewId(reviewId);

        Product product = review.getProduct();

        ProductImage productImage = productImageRepository
                .findByProductAndIsMainTrue(product)
                .orElseThrow(ProductImageNotFoundException::new);

        return ImageReviewDetailResponse.from(review, reviewImages, totalLikes, productImage);
    }

    private Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);
    }

}
