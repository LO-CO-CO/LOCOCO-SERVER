package com.lokoko.domain.media.image.domain.repository;

import com.lokoko.domain.media.image.domain.entity.ReviewImage;
import com.lokoko.domain.productReview.domain.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long>, ReviewImageRepositoryCustom {
    void deleteAllByReview(Review review);

    List<ReviewImage> findByReviewId(Long reviewId);
}
