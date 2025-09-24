package com.lokoko.domain.image.domain.repository;

import com.lokoko.domain.productReview.api.dto.response.MainImageReview;
import java.util.List;

public interface ReviewImageRepositoryCustom {
    List<MainImageReview> findMainImageReviewSorted();
}