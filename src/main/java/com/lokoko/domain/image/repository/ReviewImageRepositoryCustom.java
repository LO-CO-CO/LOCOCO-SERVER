package com.lokoko.domain.image.repository;

import com.lokoko.domain.review.api.dto.response.MainImageReview;

import java.util.List;

public interface ReviewImageRepositoryCustom {
    List<MainImageReview> findMainImageReviewSorted();
}