package com.lokoko.global.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class CookieConfig {
    
    @Value("${lokoko.jwt.cookieMaxAge}")
    private Long cookieMaxAge;

    @Value("${lokoko.jwt.secureOption}")
    private boolean secureOption;

    @Value("${lokoko.jwt.cookiePathOption}")
    private String cookiePathOption;

    @Value("${lokoko.jwt.cookieDomain}")
    private String cookieDomain;
}
