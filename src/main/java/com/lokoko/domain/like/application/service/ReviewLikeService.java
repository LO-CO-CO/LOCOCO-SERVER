package com.lokoko.domain.like.application.service;

import com.lokoko.domain.like.domain.entity.ReviewLike;
import com.lokoko.domain.like.domain.repository.ReviewLikeCountRepository;
import com.lokoko.domain.like.domain.repository.ReviewLikeRepository;
import com.lokoko.domain.productReview.application.event.PopularReviewsCacheEvictEvent;
import com.lokoko.domain.productReview.domain.entity.Review;
import com.lokoko.domain.productReview.domain.repository.ReviewRepository;
import com.lokoko.domain.productReview.exception.ReviewNotFoundException;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.global.common.annotation.DistributedLock;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewLikeCountRepository reviewLikeCountRepository;

    private final ApplicationEventPublisher eventPublisher;

    @DistributedLock(key = "'like:review:' + #reviewId + ':user:' + #userId")
    @Transactional
    public long toggleReviewLike(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Optional<ReviewLike> existing = reviewLikeRepository
                .findByReviewIdAndUserId(reviewId, userId);
        if (existing.isPresent()) {
            reviewLikeRepository.delete(existing.get());
            reviewLikeCountRepository.decrease(reviewId);
        } else {
            reviewLikeRepository.save(ReviewLike.of(review, user));
            reviewLikeCountRepository.increase(reviewId);
        }

        eventPublisher.publishEvent(new PopularReviewsCacheEvictEvent());

        return reviewLikeRepository.countByReviewId(reviewId);
    }
}
