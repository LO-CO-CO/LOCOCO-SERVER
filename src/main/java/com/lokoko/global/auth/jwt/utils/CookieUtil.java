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
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void deleteCookie(String name, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .domain(cookieConfig.getCookieDomain())
                .maxAge(0)
                .path(cookieConfig.getCookiePathOption())
                .secure(cookieConfig.isSecureOption())
                .httpOnly(true)
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
