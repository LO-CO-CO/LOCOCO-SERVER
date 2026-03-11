package com.lokoko.domain.campaignReview.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;

@Service
public class CreatorCampaignStatusResolver {

    public ParticipationStatus determineNextStatus(
            CreatorCampaign creatorCampaign,
            Campaign campaign,
            boolean simplifiedReviewFlow,
            boolean firstExists,
            boolean secondExists,
            List<ContentType> uploadedFirstRoundTypes
    ) {
        if (!Boolean.TRUE.equals(creatorCampaign.getAddressConfirmed())) {
            return ParticipationStatus.APPROVED;
        }
        if (simplifiedReviewFlow && firstExists) {
            return uploadedFirstRoundTypes.containsAll(getRequiredContentTypes(campaign))
                    ? ParticipationStatus.COMPLETED
                    : ParticipationStatus.ACTIVE;
        }
        if (secondExists) {
            return firstExists ? ParticipationStatus.COMPLETED : ParticipationStatus.ACTIVE;
        }
        return ParticipationStatus.ACTIVE;
    }

    private List<ContentType> getRequiredContentTypes(Campaign campaign) {
        List<ContentType> requiredTypes = new ArrayList<>();
        if (campaign.getFirstContentPlatform() != null) {
            requiredTypes.add(campaign.getFirstContentPlatform());
        }
        if (campaign.getSecondContentPlatform() != null) {
            requiredTypes.add(campaign.getSecondContentPlatform());
        }
        return requiredTypes;
    }
}
