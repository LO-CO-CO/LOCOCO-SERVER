package com.lokoko.domain.creator.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record CreatorSnsLinkResponse(
        @Schema(requiredMode = REQUIRED)
        String instaLink,
        @Schema(requiredMode = REQUIRED)
        String tiktokLink
) {
}