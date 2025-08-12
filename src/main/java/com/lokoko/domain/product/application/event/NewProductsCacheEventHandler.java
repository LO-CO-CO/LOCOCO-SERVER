package com.lokoko.domain.product.application.event;

import com.lokoko.domain.product.application.cache.NewProductsCacheManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NewProductsCacheEventHandler {
    
    private final NewProductsCacheManager newProductsCacheManager;
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCacheEviction(NewProductsCacheEvictEvent event) {
        newProductsCacheManager.evictCategoryCache(event.getMiddleCategory());
    }
}