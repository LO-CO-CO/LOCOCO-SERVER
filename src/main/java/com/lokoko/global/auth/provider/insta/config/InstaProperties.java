package com.lokoko.global.auth.provider.insta.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "instagram")
public record InstaProperties(
        String clientId,
        String clientSecret,
        String redirectUri,
        String scope
) {
}
