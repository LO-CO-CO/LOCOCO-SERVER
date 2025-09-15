package com.lokoko.domain.creatorCampaign.application.mapper;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class CreatorCampaignMapper {

    public CreatorCampaign toCampaignParticipation(Creator creator, Campaign campaign, Instant now) {
        return CreatorCampaign.builder()
                .creator(creator)
                .campaign(campaign)
                .status(ParticipationStatus.PENDING)
                .appliedAt(now)
                .addressConfirmed(false)
                .build();
    }
}
