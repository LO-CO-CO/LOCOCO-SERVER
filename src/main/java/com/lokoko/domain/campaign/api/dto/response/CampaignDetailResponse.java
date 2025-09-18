package com.lokoko.domain.campaign.api.dto.response;

import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignDetailPageStatus;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignType;
import com.lokoko.global.common.enums.Language;

import java.time.Instant;
import java.util.List;

public record CampaignDetailResponse(

        Long campaignId,
        CampaignType campaignType,
        String title,
        String brandImageUrl,
        String brandName,
        Language language,
        Instant applyStartDate,
        Instant applyDeadline,
        Instant creatorAnnouncementDate,
        Instant reviewSubmissionDeadline,
        List<String> deliverableRequirements,
        List<String> participationRewards,
        List<String> eligibilityRequirements,
        List<CampaignImageResponse> topImages,
        List<CampaignImageResponse> bottomImages,
        String campaignStatusCode
) {

    public static CampaignDetailResponse of(Campaign campaign, List<CampaignImageResponse> topImages,
                                            List<CampaignImageResponse> bottomImages,
                                            CampaignDetailPageStatus campaignStatusCode) {

        Brand brand = campaign.getBrand();
        return new CampaignDetailResponse(
                campaign.getId(),
                campaign.getCampaignType(),
                campaign.getTitle(),
                brand.getUser().getProfileImageUrl(),
                brand.getBrandName(),
                campaign.getLanguage(),
                campaign.getApplyStartDate(),
                campaign.getApplyDeadline(),
                campaign.getCreatorAnnouncementDate(),
                campaign.getReviewSubmissionDeadline(),
                campaign.getDeliverableRequirements(),
                campaign.getParticipationRewards(),
                campaign.getEligibilityRequirements(),
                topImages,
                bottomImages,
                campaignStatusCode.getCode()
        );
    }
}
