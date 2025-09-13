package com.lokoko.global.auth.tiktok.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Redis에 저장될 TikTok 토큰 정보
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TikTokStoredTokenDto {
    private String accessToken;
    private String refreshToken;
    private Instant accessTokenExpiresAt;
    private Instant refreshTokenExpiresAt;
    private Instant createdAt;

    public static TikTokStoredTokenDto of(String accessToken, String refreshToken,
                                         Long expiresIn, Long refreshExpiresIn) {
        Instant now = Instant.now();
        return new TikTokStoredTokenDto(
            accessToken,
            refreshToken,
            now.plusSeconds(expiresIn),
            now.plusSeconds(refreshExpiresIn),
            now
        );
    }
}