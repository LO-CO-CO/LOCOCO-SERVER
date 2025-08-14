package com.lokoko.domain.product.application.cache;

import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularProductsCacheManager {

    @CacheEvict(value = "popularProducts", key = "#category.name() + ':top4'")
    public void evictCategoryCache(MiddleCategory category) {

    }

    @CacheEvict(value = "popularProducts", allEntries = true)
    public void evictAllPopularProductsCache() {

    }

    // 🎯 더보기 페이지 캐시 무효화 추가 (카테고리별)
    @CacheEvict(value = "morePageProducts", allEntries = true)
    public void evictMorePageCacheForCategory(MiddleCategory category) {
        // 현재 Spring의 제약으로 전체 더보기 캐시 무효화
        // TODO: 향후 개선 시 카테고리별 선택적 무효화 고려
    }

    @CacheEvict(value = "morePageProducts", allEntries = true)
    public void evictAllMorePageProductsCache() {
        // 모든 더보기 페이지 캐시 무효화
    }


}
