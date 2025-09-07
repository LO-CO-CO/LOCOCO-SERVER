package com.lokoko.domain.campaign.api.dto.response;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignType;
import com.lokoko.global.common.enums.Language;

import java.time.Instant;
import java.util.List;

public record CampaignCreateResponse(
        Long campaignId,
        String campaignTitle,
        Language language,
        CampaignType campaignType,
        List<CampaignImageResponse> topImages,
        List<CampaignImageResponse> bottomImages,
        Instant applyStartDate,
        Instant applyDeadline,
        Instant creatorAnnouncementDate,
        Instant reviewSubmissionDeadline,
        int recruitmentNumber,
        List<String> participationRewards,
        List<String> deliverableRequirements,
        List<String> eligibilityRequirements
) {
    public static CampaignCreateResponse of(Campaign campaign, List<CampaignImageResponse> topImages,
                                            List<CampaignImageResponse> bottomImages) {
        return new CampaignCreateResponse(
                campaign.getId(),
                campaign.getTitle(),
                campaign.getLanguage(),
                campaign.getCampaignType(),
                topImages,
                bottomImages,
                campaign.getApplyStartDate(),
                campaign.getApplyDeadline(),
                campaign.getCreatorAnnouncementDate(),
                campaign.getReviewSubmissionDeadline(),
                campaign.getRecruitmentNumber(),
                campaign.getParticipationRewards(),
                campaign.getDeliverableRequirements(),
                campaign.getEligibilityRequirements()

        );
    }
}
