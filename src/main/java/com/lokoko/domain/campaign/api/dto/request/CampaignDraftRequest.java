package com.lokoko.domain.campaign.api.dto.request;

import com.lokoko.domain.campaign.domain.entity.enums.CampaignLanguage;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductType;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignType;
import com.lokoko.domain.socialclip.domain.entity.enums.ContentType;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public record CampaignDraftRequest(
        String campaignTitle,
        CampaignLanguage language,
        CampaignType campaignType,
        CampaignProductType campaignProductType,
        @Size(max = 5, message = "상단 이미지는 최대 5개까지 가능합니다")
        List<CampaignImageRequest> thumbnailImages,
        @Size(max = 15, message = "하단 이미지는 최대 15개까지 가능합니다")
        List<CampaignImageRequest> detailImages,
        Instant applyStartDate,
        Instant applyDeadline,
        Instant creatorAnnouncementDate,
        Instant reviewSubmissionDeadline,
        Integer recruitmentNumber,
        List<String> participationRewards,
        List<String> deliverableRequirements,
        List<String> eligibilityRequirements,
        ContentType firstContentType,
        ContentType secondContentType
) {
}