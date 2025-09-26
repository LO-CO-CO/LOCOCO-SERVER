package com.lokoko.global.auth.provider.insta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class InstagramWebClientConfig {

    @Bean(name = "instagramWebClient")
    public WebClient instagramWebClient(WebClient.Builder builder) {

        return builder.build();
    }
}
