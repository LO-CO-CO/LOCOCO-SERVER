package com.lokoko.domain.brand.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record BrandProfileImageResponse(
        @Schema(requiredMode = REQUIRED)
        String profileImageUrl
) {
}
