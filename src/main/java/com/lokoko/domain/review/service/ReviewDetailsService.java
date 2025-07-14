package com.lokoko.domain.review.service;

import com.lokoko.domain.image.entity.ReviewImage;
import com.lokoko.domain.image.repository.ReviewImageRepository;
import com.lokoko.domain.like.repository.ReviewLikeRepository;
import com.lokoko.domain.review.dto.response.ImageReviewDetailResponse;
import com.lokoko.domain.review.dto.response.VideoReviewDetailResponse;
import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.review.exception.ReviewNotFoundException;
import com.lokoko.domain.review.exception.ReviewVideoNotFoundException;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.domain.user.entity.User;
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


    public VideoReviewDetailResponse getVideoReviewDetails(Long reviewId,
                                                           Long userId) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        ReviewVideo video = reviewVideoRepository.findByReviewId(reviewId)
                .orElseThrow(ReviewVideoNotFoundException::new);

        long totalLikes = reviewLikeRepository.countByReviewId(reviewId);

        return VideoReviewDetailResponse.from(video, totalLikes);
    }


    public ImageReviewDetailResponse getImageReviewDetails(Long reviewId, Long userId) {

        User author = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        List<ReviewImage> reviewImages = reviewImageRepository.findByReviewId(reviewId);

        long totalLikes = reviewLikeRepository.countByReviewId(reviewId);

        return ImageReviewDetailResponse.from(author, review, reviewImages, totalLikes);


    }
}
