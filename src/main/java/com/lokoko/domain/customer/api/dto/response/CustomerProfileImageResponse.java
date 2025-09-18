package com.lokoko.domain.customer.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record CustomerProfileImageResponse(
        @Schema(requiredMode = REQUIRED)
        String profileImageUrl
) {
}
