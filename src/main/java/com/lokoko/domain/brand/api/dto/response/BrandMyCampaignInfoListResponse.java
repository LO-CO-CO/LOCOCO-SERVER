package com.lokoko.domain.brand.api.dto.response;

import java.util.List;

public record BrandMyCampaignInfoListResponse(
        List<BrandMyCampaignInfoResponse> campaignInfos
) {
}
