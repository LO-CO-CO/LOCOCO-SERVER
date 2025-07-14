package com.lokoko.domain.like.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record ReviewLikeResponse(
        @Schema(requiredMode = REQUIRED)
        long likeCount
) {
}
