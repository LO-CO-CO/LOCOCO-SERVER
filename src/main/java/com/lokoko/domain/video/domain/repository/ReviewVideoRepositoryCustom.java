package com.lokoko.domain.video.domain.repository;

import com.lokoko.domain.review.api.dto.response.MainVideoReview;
import java.util.List;

public interface ReviewVideoRepositoryCustom {
    List<MainVideoReview> findMainVideoReviewSorted();
}
