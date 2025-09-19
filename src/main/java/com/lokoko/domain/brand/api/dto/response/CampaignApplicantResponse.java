package com.lokoko.domain.brand.api.dto.response;

import java.time.Instant;

public record CampaignApplicantResponse(
        Long applicantId,
        Long creatorId,
        String creatorProfileImageUrl,
        String creatorFullName,
        String creatorNickName,
        Integer instagramFollower,
        Integer tiktokFollower,
        Integer participationCount,
        Instant appliedDate,
        String approveStatus
) {
}
