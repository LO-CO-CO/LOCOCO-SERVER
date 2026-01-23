package com.lokoko.domain.like.domain.repository;

import com.lokoko.domain.like.domain.entity.ReviewLikeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewLikeCountRepository extends JpaRepository<ReviewLikeCount, Long> {

    // 좋아요 수 증가
    @Query(value = "UPDATE review_like_count SET like_count = like_count + 1 " +
                   "WHERE review_id = :reviewId",
            nativeQuery = true
    )
    @Modifying
    int increase(@Param("reviewId") Long reviewId);

    // 좋아요 수 감소
    @Query(value = "UPDATE review_like_count SET like_count = CASE WHEN like_count > 0 THEN like_count -1 " +
                   "ELSE 0 END WHERE review_id = :reviewId",
            nativeQuery = true
    )
    @Modifying
    int decrease(@Param("reviewId") Long reviewId);

}
