package com.lokoko.global.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record PresignedUrlResponse(
        @Schema(requiredMode = REQUIRED)
        String presignedUrl
) {
}
