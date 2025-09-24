package com.lokoko.domain.campaign.application.mapper;

import com.lokoko.domain.campaign.api.dto.response.CampaignParticipatedResponse;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import org.springframework.stereotype.Component;

@Component
public class CampaignMapper {

    public CampaignParticipatedResponse toCampaignParticipationResponse(CreatorCampaign participation) {
        Campaign campaign = participation.getCampaign();
        return CampaignParticipatedResponse.builder()
                .campaignId(campaign.getId())
                .title(campaign.getTitle())
                .build();
    }
}
