package com.lokoko.domain.campaign.application.mapper;

import com.lokoko.domain.brand.api.dto.response.BrandIssuedCampaignResponse;
import com.lokoko.domain.campaign.api.dto.response.CampaignParticipatedResponse;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class CampaignMapper {

    public CampaignParticipatedResponse toCampaignParticipationResponse(
            CreatorCampaign participation,
            ReviewRound nextRound,
            String brandNote,
            Instant revisionRequestedAt
    ) {
        Campaign campaign = participation.getCampaign();
        return CampaignParticipatedResponse.builder()
                .campaignId(campaign.getId())
                .title(campaign.getTitle())
                .firstContentPlatform(campaign.getFirstContentPlatform())
                .secondContentPlatform(campaign.getSecondContentPlatform())
                .nowReviewRound(nextRound)
                .brandNote(brandNote)
                .revisionRequestedAt(revisionRequestedAt)
                .build();
    }

    public CampaignParticipatedResponse toCampaignParticipationResponse(
            CreatorCampaign participation,
            ReviewRound nextRound
    ) {
        return toCampaignParticipationResponse(participation, nextRound, null, null);
    }

    public BrandIssuedCampaignResponse toBrandIssuedCampaignResponse(Campaign campaign) {
        return BrandIssuedCampaignResponse.builder()
                .campaignId(campaign.getId())
                .title(campaign.getTitle())
                .firstContentPlatform(campaign.getFirstContentPlatform())
                .secondContentPlatform(campaign.getSecondContentPlatform())
                .brandNote(null)
                .revisionRequestedAt(null)
                .build();
    }
    
    public BrandIssuedCampaignResponse toBrandIssuedCampaignResponse(
            Campaign campaign, String brandNote, Instant revisionRequestedAt) {
        return BrandIssuedCampaignResponse.builder()
                .campaignId(campaign.getId())
                .title(campaign.getTitle())
                .firstContentPlatform(campaign.getFirstContentPlatform())
                .secondContentPlatform(campaign.getSecondContentPlatform())
                .brandNote(brandNote)
                .revisionRequestedAt(revisionRequestedAt)
                .build();
    }
}
