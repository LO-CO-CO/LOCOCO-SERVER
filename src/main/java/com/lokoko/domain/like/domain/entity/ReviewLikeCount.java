package com.lokoko.domain.like.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReviewLikeCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_like_count_id")
    private Long id;

    private Long reviewId;

    private long likeCount;

    public static ReviewLikeCount init(Long reviewId) {
        return ReviewLikeCount.builder()
                .reviewId(reviewId)
                .build();
    }

}
