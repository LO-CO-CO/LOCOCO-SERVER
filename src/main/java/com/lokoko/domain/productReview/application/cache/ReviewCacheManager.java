package com.lokoko.domain.productReview.application.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewCacheManager {

    @CacheEvict(value = {"popularImageReviews", "popularVideoReviews"}, allEntries = true)
    public void evictAllReviewCaches() {
    }

    @CacheEvict(value = "popularImageReviews", allEntries = true)
    public void evictImageReviewCache() {
    }

    @CacheEvict(value = "popularVideoReviews", allEntries = true)
    public void evictVideoReviewCache() {
    }
}