package com.lokoko.global.auth.service;

import com.lokoko.global.auth.exception.StateValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OAuthStateManager {

    private final RedisTemplate<String, String> redisTemplate;

    public String generateState(Long creatorId) {
        String state = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(
                "oauth:state:" + state,
                String.valueOf(creatorId),
                5, TimeUnit.MINUTES
        );

        return state;
    }

    public String generateStateWithReturnTo(Long creatorId, String returnTo) {
        String state = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(
                "oauth:state:" + state,
                String.valueOf(creatorId),
                5, TimeUnit.MINUTES
        );

        redisTemplate.opsForValue().set(
                "oauth:returnTo:" + state,
                returnTo,
                5, TimeUnit.MINUTES
        );

        return state;
    }

    public Long validateAndGetCreatorId(String state) {
        String key = "oauth:state:" + state;
        String creatorId = redisTemplate.opsForValue().get(key);

        if (creatorId == null) {
           throw StateValidationException.invalid();
        }

        redisTemplate.delete(key);
        return Long.valueOf(creatorId);
    }

    public String getReturnTo(String state) {
        String key = "oauth:returnTo:" + state;
        String returnTo = redisTemplate.opsForValue().get(key);
        redisTemplate.delete(key);
        return returnTo;
    }
}
