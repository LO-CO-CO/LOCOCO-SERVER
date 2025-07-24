package com.lokoko.domain.video.repository;

import com.lokoko.domain.review.dto.response.MainVideoReview;

import java.util.List;

public interface ReviewVideoRepositoryCustom {
    List<MainVideoReview> findMainVideoReviewSorted();
}
