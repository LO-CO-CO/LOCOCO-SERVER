package com.lokoko.domain.like.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record ToggleLikeResponse(
        @Schema(requiredMode = REQUIRED)
        Boolean isLiked
) {
}
