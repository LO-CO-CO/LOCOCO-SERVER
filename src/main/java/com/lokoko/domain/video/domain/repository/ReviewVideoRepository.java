package com.lokoko.domain.video.domain.repository;

import com.lokoko.domain.review.domain.entity.Review;
import com.lokoko.domain.video.domain.entity.ReviewVideo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewVideoRepository extends JpaRepository<ReviewVideo, Long>, ReviewVideoRepositoryCustom {
    void deleteAllByReview(Review review);

    Optional<ReviewVideo> findByReviewId(Long reviewId);

    List<ReviewVideo> findAllByReviewId(Long reviewId);
}
