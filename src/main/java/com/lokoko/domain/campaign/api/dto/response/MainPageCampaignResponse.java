package com.lokoko.domain.campaign.api.dto.response;

import com.lokoko.domain.campaign.domain.entity.enums.CampaignLanguage;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignType;


import java.time.Instant;

public record MainPageCampaignResponse(
        Long campaignId,
        CampaignType campaignType,
        CampaignLanguage language,
        String brandName,
        String campaignImageUrl,
        String campaignName,
        Integer applicantNumber,
        Integer recruitmentNumber,
        Instant endTime,
        String chipStatus
) {
}
