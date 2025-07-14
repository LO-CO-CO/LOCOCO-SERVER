package com.lokoko.global.auth.jwt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
public record JwtTokenResponse(
        @Schema(requiredMode = REQUIRED)
        String accessToken,
        @Schema(requiredMode = REQUIRED)
        String refreshToken,
        @Schema(requiredMode = REQUIRED)
        String tokenId
) {
    public static JwtTokenResponse of(String accessToken, String refreshToken, String tokenId) {
        return new JwtTokenResponse(accessToken, refreshToken, tokenId);
    }

    public static JwtTokenResponse of(String accessToken, String refreshToken) {
        return new JwtTokenResponse(accessToken, refreshToken, null);
    }
}
