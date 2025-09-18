package com.lokoko.domain.brand.api.dto.response;

import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;

import java.time.Instant;

public record BrandMyCampaignResponse(
        Long id,
        String campaignImageUrl,
        String title,
        Instant applyDeadline,
        Integer applicantNumber,
        Integer recruitmentNumber,
        String campaignStatus
) {
}
