package com.lokoko.domain.campaign.api.dto.response;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignLanguage;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductType;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignType;
import com.lokoko.domain.socialclip.domain.entity.enums.ContentType;

import java.time.Instant;
import java.util.List;

public record CampaignBasicResponse(
        Long campaignId,
        String campaignTitle,
        CampaignLanguage language,
        CampaignType campaignType,
        CampaignProductType campaignProductType,
        List<CampaignImageResponse> topImages,
        List<CampaignImageResponse> bottomImages,
        Instant applyStartDate,
        Instant applyDeadline,
        Instant creatorAnnouncementDate,
        Instant reviewSubmissionDeadline,
        int recruitmentNumber,
        List<String> participationRewards,
        List<String> deliverableRequirements,
        List<String> eligibilityRequirements,
        ContentType firstContentType,
        ContentType secondContentType
) {
    public static CampaignBasicResponse of(Campaign campaign, List<CampaignImageResponse> topImages,
                                           List<CampaignImageResponse> bottomImages) {
        return new CampaignBasicResponse(
                campaign.getId(),
                campaign.getTitle(),
                campaign.getLanguage(),
                campaign.getCampaignType(),
                campaign.getCampaignProductType(),
                topImages,
                bottomImages,
                campaign.getApplyStartDate(),
                campaign.getApplyDeadline(),
                campaign.getCreatorAnnouncementDate(),
                campaign.getReviewSubmissionDeadline(),
                campaign.getRecruitmentNumber(),
                campaign.getParticipationRewards(),
                campaign.getDeliverableRequirements(),
                campaign.getEligibilityRequirements(),
                campaign.getFirstContentPlatform(),
                campaign.getSecondContentPlatform()

        );
    }
}
