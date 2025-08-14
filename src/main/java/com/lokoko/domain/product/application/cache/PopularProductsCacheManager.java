package com.lokoko.domain.product.application.cache;

import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import com.lokoko.global.utils.RedisCacheUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PopularProductsCacheManager {

    private final RedisCacheUtil redisCacheUtil;

    /**
     * 특정 카테고리의 메인페이지 인기상품 캐시를 무효화.
     */
    @CacheEvict(value = "popularProducts", key = "#category.name() + ':top4'")
    public void evictCategoryCache(MiddleCategory category) {
        log.debug("Evicted popular products cache for category: {}", category);
    }

    /**
     * 특정 카테고리의 인기상품 더보기 캐시만 정밀 무효화
     * 패턴 매칭을 사용하여 해당 카테고리의 모든 페이지를 삭제
     */
    public void evictPopularMorePageCacheForCategory(MiddleCategory category) {
        String pattern = "morePageProducts::" + category.name() + ":popular:*";
        long deletedCount = redisCacheUtil.deleteByPattern(pattern);
        log.info("Evicted {} popular more-page cache entries for category: {}", deletedCount, category);
    }

}
