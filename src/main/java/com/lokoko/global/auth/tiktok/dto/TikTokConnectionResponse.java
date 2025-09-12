package com.lokoko.global.auth.tiktok.dto;

import java.time.Instant;

public record TikTokConnectionResponse(
        boolean connected,
        String tikTokUserId,
        Instant connectedAt
) {
    public static TikTokConnectionResponse connected(String userId, Instant connectedAt) {
        return new TikTokConnectionResponse(true, userId, connectedAt);
    }
}