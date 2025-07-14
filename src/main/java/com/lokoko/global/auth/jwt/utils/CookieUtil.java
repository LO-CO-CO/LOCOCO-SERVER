package com.lokoko.global.auth.jwt.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    @Value("${lokoko.jwt.cookieMaxAge}")
    private Long cookieMaxAge;

    @Value("${lokoko.jwt.secureOption}")
    private boolean secureOption;

    @Value("${lokoko.jwt.cookiePathOption}")
    private String cookiePathOption;

    @Value("${lokoko.jwt.cookieDomain}")
    private String cookieDomain;

    public void setCookie(String name, String value, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .domain(cookieDomain)
                .maxAge(cookieMaxAge)
                .path(cookiePathOption)
                .secure(secureOption)
                .httpOnly(true)
                .sameSite("none")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "value")
                .domain(cookieDomain)
                .maxAge(0)
                .path(cookiePathOption)
                .secure(secureOption)
                .httpOnly(true)
                .sameSite("none")
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
    }
}
