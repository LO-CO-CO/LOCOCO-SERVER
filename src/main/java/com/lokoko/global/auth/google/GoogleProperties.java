package com.lokoko.global.auth.google;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "google")
public record GoogleProperties(
        String clientId,
        String clientSecret,
        String redirectUri,
        String baseUrl,
        String scope
) {
}