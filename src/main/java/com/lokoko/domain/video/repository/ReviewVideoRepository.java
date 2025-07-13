package com.lokoko.domain.video.repository;

import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.video.entity.ReviewVideo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewVideoRepository extends JpaRepository<ReviewVideo, Long>, ReviewVideoRepositoryCustom {
    void deleteAllByReview(Review review);

    Optional<ReviewVideo> findByReviewId(Long reviewId);
}
