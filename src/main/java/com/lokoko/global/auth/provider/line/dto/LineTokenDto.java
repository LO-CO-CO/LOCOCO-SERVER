package com.lokoko.global.auth.provider.line.dto;

public record LineTokenDto(
        String access_token,
        String refresh_token,
        Long expires_in,
        String id_token
) {
}
