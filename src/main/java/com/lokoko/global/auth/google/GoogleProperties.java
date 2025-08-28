package com.lokoko.global.auth.google;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google")
@Getter
@Setter
public class GoogleProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String baseUrl;
    private String scope;
}