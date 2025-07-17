package com.lokoko.domain.like.repository;

import com.lokoko.domain.like.entity.ReviewLike;
import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByReviewIdAndUserId(Long reviewId, Long userId);

    long countByReviewId(Long reviewId);

    boolean existsByUserAndReview(User user, Review review);

    void deleteAllByReview(Review review);
}
