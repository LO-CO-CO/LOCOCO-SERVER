package com.lokoko.domain.product.application.cache;

import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewProductsCacheManager {
    
    @CacheEvict(value = "newProducts", key = "#category.name() + ':top4'")
    public void evictCategoryCache(MiddleCategory category) {
    }
    
    @CacheEvict(value = "newProducts", allEntries = true)
    public void evictAllNewProductsCache() {
    }

    @CacheEvict(value = "morePageProducts", allEntries = true)
    public void evictMorePageCacheForCategory(MiddleCategory category) {
    }

    @CacheEvict(value = "morePageProducts", allEntries = true)
    public void evictAllMorePageProductsCache() {
    }
}