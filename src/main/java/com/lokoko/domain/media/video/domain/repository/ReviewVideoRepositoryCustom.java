package com.lokoko.domain.media.video.domain.repository;

import com.lokoko.domain.productReview.api.dto.response.MainVideoReview;
import java.util.List;

public interface ReviewVideoRepositoryCustom {
    List<MainVideoReview> findMainVideoReviewSorted();
}
