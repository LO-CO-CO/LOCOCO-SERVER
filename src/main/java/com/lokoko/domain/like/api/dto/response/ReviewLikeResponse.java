package com.lokoko.domain.like.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record ReviewLikeResponse(
        @Schema(requiredMode = REQUIRED)
        Long likeCount
) {
}
