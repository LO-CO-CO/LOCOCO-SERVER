package com.lokoko.domain.user.application.service;

import com.lokoko.domain.user.api.dto.request.AdminLoginRequest;
import com.lokoko.domain.user.api.dto.response.AdminLoginResponse;
import com.lokoko.domain.user.domain.repository.AdminRepository;
import com.lokoko.global.auth.entity.Admin;
import com.lokoko.global.auth.exception.AdminLoginFailedException;
import com.lokoko.global.auth.jwt.utils.CookieUtil;
import com.lokoko.global.auth.jwt.utils.JwtProvider;
import com.lokoko.global.utils.RedisUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.lokoko.global.auth.jwt.utils.JwtProvider.*;


@Service
@RequiredArgsConstructor
public class AdminLoginService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CookieUtil cookieUtil;
    private final RedisUtil redisUtil;

    @Value("${lokoko.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    private static final String REFRESH_TOKEN_KEY_PREFIX = "refreshToken:";

    public AdminLoginResponse login(AdminLoginRequest loginRequest, HttpServletResponse servletResponse) {

        Admin admin = adminRepository.findByLoginId(loginRequest.loginId())
                .orElseThrow(AdminLoginFailedException::new);

        validatePassword(loginRequest, admin);

        String tokenId = UUID.randomUUID().toString();
        String accessToken = jwtProvider.generateAccessToken(admin.getId(), "ADMIN", null);
        String refreshToken = jwtProvider.generateRefreshToken(admin.getId(), "ADMIN", tokenId, null);

        setRefreshTokenInRedis(admin, refreshToken);

        setCookie(servletResponse, accessToken, refreshToken);

        return new AdminLoginResponse(accessToken, refreshToken, admin.getId(), "ADMIN");
    }

    private void validatePassword(AdminLoginRequest loginRequest, Admin admin) {
        if (!passwordEncoder.matches(loginRequest.password(), admin.getPassword())) {
            throw new AdminLoginFailedException();
        }
    }

    private void setRefreshTokenInRedis(Admin admin, String refreshToken) {
        String redisKey = REFRESH_TOKEN_KEY_PREFIX + admin.getId();
        redisUtil.setRefreshToken(redisKey, refreshToken, refreshTokenExpiration);
    }

    private void setCookie(HttpServletResponse servletResponse, String accessToken, String refreshToken) {
        cookieUtil.setCookie(ACCESS_TOKEN_HEADER, accessToken, servletResponse);
        cookieUtil.setCookie(REFRESH_TOKEN_HEADER, refreshToken, servletResponse);
    }
}
