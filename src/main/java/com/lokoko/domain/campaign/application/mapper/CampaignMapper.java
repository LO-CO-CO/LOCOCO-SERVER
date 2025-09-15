package com.lokoko.domain.campaign.application.mapper;

import com.lokoko.domain.campaign.api.dto.response.ParticipatingCampaignResponse;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.CreatorCampaign;
import org.springframework.stereotype.Component;

@Component
public class CampaignMapper {

    public ParticipatingCampaignResponse toCampaignParticipationResponse(CreatorCampaign participation) {
        Campaign campaign = participation.getCampaign();
        return ParticipatingCampaignResponse.builder()
                .campaignId(campaign.getId())
                .title(campaign.getTitle())
                .build();
    }
}
