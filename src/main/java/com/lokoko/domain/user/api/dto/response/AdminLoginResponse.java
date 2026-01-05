package com.lokoko.domain.user.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record AdminLoginResponse(
        @Schema(requiredMode = REQUIRED)
        String accessToken,
        @Schema(requiredMode = REQUIRED)
        String refreshToken,
        @Schema(requiredMode = REQUIRED)
        Long userId,
        @Schema(requiredMode = REQUIRED)
        String role
) {
}
