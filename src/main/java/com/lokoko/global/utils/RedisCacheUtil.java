package com.lokoko.global.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisCacheUtil {

    private final RedisTemplate<String, Object> redisObjectTemplate;

    /**
     * 패턴에 맞는 캐시 키들을 모두 삭제.
     * @param pattern Redis 패턴 (예: "morePageProducts::SKINCARE:*")
     * @return 삭제된 키의 개수
     */
    public long deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisObjectTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                Long deletedCount = redisObjectTemplate.delete(keys);
                log.info("Deleted {} cache keys matching pattern: {}", deletedCount, pattern);
                return deletedCount != null ? deletedCount : 0;
            }
            log.debug("No cache keys found matching pattern: {}", pattern);
            return 0;
        } catch (Exception e) {
            log.error("Failed to delete cache keys by pattern: {}", pattern, e);
            return 0;
        }
    }
}