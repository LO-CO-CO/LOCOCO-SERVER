package com.lokoko.global.config;

import com.lokoko.global.utils.GoogleConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${line.base-url}")
    private String lineBaseUrl;

    @Value("${google.base-url}")
    private String googleBaseUrl;

    @Value("${tiktok.base-url}")
    private String tikTokBaseUrl;

    @Bean
    public WebClient lineWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(lineBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

    @Bean
    public WebClient googleWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(googleBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

    @Bean
    public WebClient googleUserInfoWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(GoogleConstants.USERINFO_BASE_URL)
                .build();
    }

    @Bean
    public WebClient tikTokWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(tikTokBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }
}
