package com.lokoko.domain.product.application.event;

import com.lokoko.domain.product.application.cache.PopularProductsCacheManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PopularProductsCacheEventHandler {
    
    private final PopularProductsCacheManager popularProductsCacheManager;
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCacheEviction(PopularProductsCacheEvictEvent event) {
        // 메인페이지 캐시 무효화
        popularProductsCacheManager.evictCategoryCache(event.getMiddleCategory());
        // 더보기 페이지 캐시 무효화
        popularProductsCacheManager.evictMorePageCacheForCategory(event.getMiddleCategory());
    }
}