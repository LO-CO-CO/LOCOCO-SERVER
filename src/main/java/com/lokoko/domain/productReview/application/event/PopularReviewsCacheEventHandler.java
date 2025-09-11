package com.lokoko.domain.productReview.application.event;

import com.lokoko.domain.productReview.application.cache.ReviewCacheManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PopularReviewsCacheEventHandler {

    private final ReviewCacheManager reviewCacheManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCacheEviction(PopularReviewsCacheEvictEvent event) {
        reviewCacheManager.evictAllReviewCaches();
    }
}