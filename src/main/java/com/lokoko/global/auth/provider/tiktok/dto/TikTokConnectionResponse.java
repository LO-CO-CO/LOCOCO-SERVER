package com.lokoko.global.auth.provider.tiktok.dto;

public record TikTokConnectionResponse(
        boolean connected,
        String tikTokUserId,
        String redirectUrl
) {
    public static TikTokConnectionResponse connected(String userId, String redirectUrl) {
        return new TikTokConnectionResponse(true, userId, redirectUrl);
    }
}