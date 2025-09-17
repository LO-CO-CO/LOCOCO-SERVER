package com.lokoko.domain.brand.api.dto.response;

import com.lokoko.domain.campaignReview.domain.entity.enums.BrandNoteStatus;

import java.time.Instant;

public record BrandNoteRevisionResponse(
        String brandNote,
        BrandNoteStatus status,
        Instant revisionRequestedAt
) {
}
