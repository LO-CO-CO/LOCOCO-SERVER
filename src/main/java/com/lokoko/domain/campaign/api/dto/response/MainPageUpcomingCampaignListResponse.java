package com.lokoko.domain.campaign.api.dto.response;

import com.lokoko.global.common.response.PageableResponse;

import java.util.List;

public record MainPageUpcomingCampaignListResponse(
        List<MainPageUpcomingCampaignResponse> campaigns,
        PageableResponse pageInfo
) {
}
