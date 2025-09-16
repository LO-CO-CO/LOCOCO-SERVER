package com.lokoko.domain.campaign.api.dto.response;

import com.lokoko.global.common.response.PageableResponse;

import java.util.List;

public record MainPageCampaignListResponse(
        List<MainPageCampaignResponse> campaigns,
        PageableResponse pageInfo
) {
}
