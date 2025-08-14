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

    @CacheEvict(value = "morePageProducts", allEntries = true)
    public void evictMorePageCacheForCategory(MiddleCategory category) {
    }

    @CacheEvict(value = "morePageProducts", allEntries = true)
    public void evictAllMorePageProductsCache() {
    }


}
