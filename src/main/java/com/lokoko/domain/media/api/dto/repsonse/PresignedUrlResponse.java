package com.lokoko.domain.media.api.dto.repsonse;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;

public record PresignedUrlResponse(

        @Schema(requiredMode = REQUIRED)
        String presignedUrl
) {
}
