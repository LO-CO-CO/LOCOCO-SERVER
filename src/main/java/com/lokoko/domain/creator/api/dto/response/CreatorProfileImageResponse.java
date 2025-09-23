package com.lokoko.domain.creator.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreatorProfileImageResponse(

        @Schema(requiredMode = REQUIRED, description = "크리에이터 프로필 이미지 URL")
        String profileImageUrl
) {
}
