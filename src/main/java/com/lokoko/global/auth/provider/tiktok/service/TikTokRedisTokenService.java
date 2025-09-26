package com.lokoko.global.auth.provider.tiktok.service;

import com.lokoko.global.auth.exception.TikTokReAuthenticationRequiredException;
import com.lokoko.global.auth.provider.tiktok.TikTokOAuthClient;
import com.lokoko.global.auth.provider.tiktok.dto.TikTokTokenDto;
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
public class TikTokRedisTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final TikTokOAuthClient tikTokOAuthClient;

    private static final String TOKEN_KEY_PREFIX = "tiktok:tokens:";
    private static final int TOKEN_TTL_DAYS = 30;

    /**
     * 토큰 저장
     */
    public void saveTokens(Long creatorId, String accessToken, String refreshToken,
                           Long expiresIn, Long refreshExpiresIn) {
        String key = getTokenKey(creatorId);
        Instant now = Instant.now();

        Map<String, Object> tokenData = Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "accessTokenExpiresAt", now.plusSeconds(expiresIn).toString(),
                "refreshTokenExpiresAt", now.plusSeconds(refreshExpiresIn).toString()
        );

        redisTemplate.opsForHash().putAll(key, tokenData);
        redisTemplate.expire(key, Duration.ofDays(TOKEN_TTL_DAYS));
    }

    /**
     * 유효한 액세스 토큰 획득 실패하면 토큰 삭제 후 재연결 요구
     */
    public String getValidAccessToken(Long creatorId) {
        String key = getTokenKey(creatorId);

        if (!redisTemplate.hasKey(key)) {
            throw new TikTokReAuthenticationRequiredException();
        }

        String accessToken = (String) redisTemplate.opsForHash().get(key, "accessToken");
        String expiresAtStr = (String) redisTemplate.opsForHash().get(key, "accessTokenExpiresAt");

        if (accessToken == null || expiresAtStr == null) {
            deleteTokens(creatorId);
            throw new TikTokReAuthenticationRequiredException();
        }

        // 액세스 토큰 만료 확인 (5분 전 미리 갱신)
        // 액세스 토큰 만료직전이면, refreshToken 을 가져와서, 틱톡에 다시 accessToken 을 요청한다.
        Instant expiresAt = Instant.parse(expiresAtStr);
        if (isAccessTokenExpiringSoon(expiresAt)) {
            String refreshToken = (String) redisTemplate.opsForHash().get(key, "refreshToken");
            if (refreshToken == null) {
                deleteTokens(creatorId);
                throw new TikTokReAuthenticationRequiredException();
            }

            refreshTokens(creatorId, refreshToken);
            accessToken = (String) redisTemplate.opsForHash().get(key, "accessToken");
        }

        // 액세스 토큰의 기간이 충분히 남았을 경우 그대로 반환
        return accessToken;
    }

    /**
     * 토큰 존재 여부 확인
     */
    public boolean hasTokens(Long creatorId) {
        return redisTemplate.hasKey(getTokenKey(creatorId));
    }

    /**
     * 토큰 삭제
     */
    public void deleteTokens(Long creatorId) {
        redisTemplate.delete(getTokenKey(creatorId));
    }

    /**
     * 액세스 토큰 만료 임박 확인
     */
    private boolean isAccessTokenExpiringSoon(Instant expiresAt) {
        Instant fiveMinutesFromNow = Instant.now().plus(5, ChronoUnit.MINUTES);
        return expiresAt.isBefore(fiveMinutesFromNow);
    }

    /**
     * 토큰 갱신
     */
    private void refreshTokens(Long creatorId, String refreshToken) {
        // 새 토큰 요청
        TikTokTokenDto newTokens = tikTokOAuthClient.refreshToken(refreshToken);

        // Redis에 저장
        saveTokens(
                creatorId,
                newTokens.accessToken(),
                newTokens.refreshToken(),
                newTokens.expiresIn(),
                newTokens.refreshExpiresIn()
        );
    }

    private String getTokenKey(Long creatorId) {
        return TOKEN_KEY_PREFIX + creatorId;
    }
}