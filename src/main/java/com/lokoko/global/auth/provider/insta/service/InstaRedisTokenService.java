package com.lokoko.global.auth.provider.insta.service;

import com.lokoko.global.auth.exception.InstagramReAuthenticationRequiredException;
import com.lokoko.global.auth.provider.insta.config.InstaOauthClient;
import com.lokoko.global.auth.provider.insta.dto.InstagramLongTokenDto;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstaRedisTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final InstaOauthClient instaOauthClient;

    private static final String TOKEN_KEY_PREFIX = "insta:tokens:";
    private static final String ACCESS_TOKEN_FIELD = "accessToken";
    private static final String EXPIRES_AT_FIELD = "accessTokenExpiresAt";

    private static final int EXPIRING_SOON_FIELD = 7;
    private static final int TOKEN_TTL_DAYS = 90;

    /**
     * 장기 토큰 저장 (발급/갱신 공통)
     */
    public void saveLongLivedToken(Long userId, String accessToken, Long expiresInSeconds) {
        String key = key(userId);
        Instant now = Instant.now();
        Instant accessTokenExpiresAt = now.plusSeconds(
                expiresInSeconds != null ? expiresInSeconds : 60L * 60 * 24 * 60);

        Map<String, Object> tokenData = Map.of(
                ACCESS_TOKEN_FIELD, accessToken,
                EXPIRES_AT_FIELD, accessTokenExpiresAt.toString()
        );

        redisTemplate.opsForHash().putAll(key, tokenData);
        redisTemplate.expire(key, Duration.ofDays(TOKEN_TTL_DAYS));
    }

    /**
     * 유효한 액세스 토큰 반환 (만료 임박 시 자동 갱신 시도) - 없거나, 이미 만료/갱신 실패 시 InstagramReAuthenticationRequiredException
     */
    public String getValidAccessToken(Long userId) {
        String key = key(userId);

        if (!redisTemplate.hasKey(key)) {
            throw new InstagramReAuthenticationRequiredException();
        }

        String accessToken = (String) redisTemplate.opsForHash().get(key, "accessToken");
        String expiresAtStr = (String) redisTemplate.opsForHash().get(key, "accessTokenExpiresAt");

        if (accessToken == null || expiresAtStr == null) {
            deleteToken(userId);
            throw new InstagramReAuthenticationRequiredException();
        }

        Instant expiresAt = Instant.parse(expiresAtStr);

        // 만료 임박하면 갱신 시도
        if (isExpiringSoon(expiresAt)) {
            try {
                InstagramLongTokenDto refreshed = instaOauthClient.refreshLongLivedToken(accessToken);
                saveLongLivedToken(userId, refreshed.accessToken(), refreshed.expiresIn());
                accessToken = refreshed.accessToken();
            } catch (Exception e) {

                log.warn("Instagram 토큰 갱신 실패. userId={}, reason={}", userId, e.getMessage());
                if (Instant.now().isAfter(expiresAt)) {
                    deleteToken(userId);

                    throw new InstagramReAuthenticationRequiredException();
                }
            }
        } else if (Instant.now().isAfter(expiresAt)) {
            deleteToken(userId);

            throw new InstagramReAuthenticationRequiredException();
        }

        return accessToken;
    }

    public boolean hasToken(Long userId) {
        return redisTemplate.hasKey(key(userId));
    }

    public void deleteToken(Long userId) {
        redisTemplate.delete(key(userId));
    }

    private boolean isExpiringSoon(Instant expiresAt) {

        Instant sevenDaysFromNow = Instant.now().plus(EXPIRING_SOON_FIELD, ChronoUnit.DAYS);
        return expiresAt.isBefore(sevenDaysFromNow);
    }

    private String key(Long userId) {
        return TOKEN_KEY_PREFIX + userId;
    }
}