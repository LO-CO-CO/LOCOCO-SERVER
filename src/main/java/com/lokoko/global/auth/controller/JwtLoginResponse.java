package com.lokoko.global.auth.controller;

import com.lokoko.global.auth.jwt.dto.JwtTokenResponse;

public record JwtLoginResponse(
        String accessToken,
        String refreshToken
) {
    public static JwtLoginResponse of(JwtTokenResponse dto) {
        return new JwtLoginResponse(dto.accessToken(), dto.refreshToken());
    }
}
