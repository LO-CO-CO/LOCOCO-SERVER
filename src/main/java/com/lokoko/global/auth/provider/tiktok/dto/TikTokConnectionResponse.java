package com.lokoko.global.auth.provider.tiktok.dto;

public record TikTokConnectionResponse(
        boolean connected,
        String tikTokUserId
) {
    public static TikTokConnectionResponse connected(String userId) {
        return new TikTokConnectionResponse(true, userId);
    }
}