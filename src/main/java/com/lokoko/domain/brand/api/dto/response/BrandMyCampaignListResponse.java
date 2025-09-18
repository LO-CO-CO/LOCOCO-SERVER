package com.lokoko.domain.brand.api.dto.response;

import com.lokoko.global.common.response.PageableResponse;

import java.util.List;

public record BrandMyCampaignListResponse(
        List<BrandMyCampaignResponse> campaigns,
        PageableResponse pageInfo
) {
}
