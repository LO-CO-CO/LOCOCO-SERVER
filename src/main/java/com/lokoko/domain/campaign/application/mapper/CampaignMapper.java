package com.lokoko.domain.campaign.application.mapper;

import com.lokoko.domain.brand.api.dto.response.BrandIssuedCampaignResponse;
import com.lokoko.domain.campaign.api.dto.response.CampaignParticipatedResponse;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CampaignMapper {

    public CampaignParticipatedResponse toCampaignParticipationResponse(
            CreatorCampaign participation,
            List<CampaignParticipatedResponse.ReviewContentStatus> reviewContents
    ) {
        Campaign campaign = participation.getCampaign();
        return CampaignParticipatedResponse.builder()
                .campaignId(campaign.getId())
                .title(campaign.getTitle())
                .reviewContents(reviewContents)
                .build();
    }

    public CampaignParticipatedResponse.ReviewContentStatus toReviewContentStatus(
            ContentType contentType,
            ReviewRound nowReviewRound,
            String brandNote,
            Instant revisionRequestedAt,
            String captionWithHashtags,
            List<String> mediaUrls
    ) {
        return CampaignParticipatedResponse.ReviewContentStatus.builder()
                .contentType(contentType)
                .nowReviewRound(nowReviewRound)
                .brandNote(brandNote)
                .revisionRequestedAt(revisionRequestedAt)
                .captionWithHashtags(captionWithHashtags)
                .mediaUrls(mediaUrls)
                .build();
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
