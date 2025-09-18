package com.lokoko.domain.brand.api.dto.response;

import com.lokoko.domain.campaignReview.domain.entity.enums.BrandNoteStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record BrandNoteRevisionResponse(
        @Schema(requiredMode = REQUIRED, example = "태그를 더 추가하세요.")
        String brandNote,
        @Schema(requiredMode = REQUIRED, example = "DRAFT")
        BrandNoteStatus status,
        @Schema(requiredMode = REQUIRED, example = "2025-09-18T10:30:00Z")
        Instant revisionRequestedAt
) {
}
