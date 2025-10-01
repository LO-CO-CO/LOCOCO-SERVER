package com.lokoko.global.auth.provider.tiktok.dto;

public record TikTokConnectionResponse(
        String redirectUrl
) {
    public static TikTokConnectionResponse connected(String redirectUrl) {
        return new TikTokConnectionResponse(redirectUrl);
    }
}