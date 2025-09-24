package com.lokoko.domain.brand.api.dto.response;

import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;

import java.time.Instant;

public record CampaignDashboard(
        Long campaignId,
        String title,
        String thumbnailUrl,
        Instant applyStartDate,
        Instant applyDeadline,
        Instant creatorAnnouncementDate,
        Instant reviewSubmissionDeadline,
        CampaignStatus savedStatus,
        Integer approvedNumber,
        Long instaPostCount,
        Long instaReelsCount,
        Long tiktokVideoCount
) {
}