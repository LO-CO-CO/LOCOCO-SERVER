package com.lokoko.global.auth.provider.tiktok.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tiktok")
public record TikTokProperties(
        String clientKey,
        String clientSecret,
        String redirectUri,
        String baseUrl,
        String scope
) {
}