package com.lokoko.domain.review.service;

import com.lokoko.domain.image.entity.ReceiptImage;
import com.lokoko.domain.image.entity.ReviewImage;
import com.lokoko.domain.image.repository.ReceiptImageRepository;
import com.lokoko.domain.image.repository.ReviewImageRepository;
import com.lokoko.domain.like.repository.ReviewLikeRepository;
import com.lokoko.domain.review.dto.response.ImageReviewDetailResponse;
import com.lokoko.domain.review.dto.response.VideoReviewDetailResponse;
import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.review.exception.ReviewNotFoundException;
import com.lokoko.domain.review.exception.ReviewVideoNotFoundException;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.entity.enums.Role;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.domain.user.repository.UserRepository;
import com.lokoko.domain.video.entity.ReviewVideo;
import com.lokoko.domain.video.repository.ReviewVideoRepository;
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


    public VideoReviewDetailResponse getVideoReviewDetails(Long reviewId,
                                                           Long userId) {
        User user = getUser(userId);
        Review review = getReview(reviewId);
        ReviewVideo video = getReviewVideo(reviewId);
        long totalLikes = reviewLikeRepository.countByReviewId(reviewId);
        ReceiptImage receiptImage = getReceiptImageIfAdmin(user, reviewId);

        return VideoReviewDetailResponse.from(video, totalLikes, receiptImage, user.getRole());
    }


    public ImageReviewDetailResponse getImageReviewDetails(Long reviewId, Long userId) {

        User user = getUser(userId);
        Review review = getReview(reviewId);
        List<ReviewImage> reviewImages = reviewImageRepository.findByReviewId(reviewId);
        long totalLikes = reviewLikeRepository.countByReviewId(reviewId);
        ReceiptImage receiptImage = getReceiptImageIfAdmin(user, reviewId);

        return ImageReviewDetailResponse.from(review, reviewImages, totalLikes, receiptImage, user.getRole());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
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
