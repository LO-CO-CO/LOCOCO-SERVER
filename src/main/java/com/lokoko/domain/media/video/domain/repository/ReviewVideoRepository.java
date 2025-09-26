package com.lokoko.domain.media.video.domain.repository;

import com.lokoko.domain.media.video.domain.entity.ReviewVideo;
import com.lokoko.domain.productReview.domain.entity.Review;
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
