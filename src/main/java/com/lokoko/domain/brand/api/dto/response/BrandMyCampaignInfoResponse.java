package com.lokoko.domain.brand.api.dto.response;

import java.time.Instant;

public record BrandMyCampaignInfoResponse(
        Long campaignId,
        String campaignTitle,
        Instant startDate,
        Instant endDate
) {
}
