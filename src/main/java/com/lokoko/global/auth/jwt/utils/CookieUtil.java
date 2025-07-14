package com.lokoko.global.auth.jwt.utils;

import com.lokoko.global.config.CookieConfig;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final CookieConfig cookieConfig;

    public void setCookie(String name, String value, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .domain(cookieConfig.getCookieDomain())
                .maxAge(cookieConfig.getCookieMaxAge())
                .path(cookieConfig.getCookiePathOption())
                .secure(cookieConfig.isSecureOption())
                .httpOnly(true)
                .sameSite("none")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "value")
                .domain(cookieConfig.getCookieDomain())
                .maxAge(0)
                .path(cookieConfig.getCookiePathOption())
                .secure(cookieConfig.isSecureOption())
                .httpOnly(true)
                .sameSite("none")
                .build();

        response.setHeader("Set-Cookie", cookie.toString());
    }
}
