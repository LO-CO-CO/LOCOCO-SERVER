package com.lokoko.domain.creator.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record CreatorSnsConnectedResponse(

        @Schema(requiredMode = REQUIRED)
        boolean isInstaConnected,

        @Schema(requiredMode = REQUIRED)
        boolean isTiktokConnected
) {
}