package com.lokoko.global.auth.provider.google.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleTokenDto(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String idToken,
        String tokenType,
        String scope
) {
}