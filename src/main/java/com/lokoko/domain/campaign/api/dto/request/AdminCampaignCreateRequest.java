package com.lokoko.domain.campaign.api.dto.request;

import com.lokoko.domain.campaign.domain.entity.enums.CampaignLanguage;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductType;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignType;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record AdminCampaignCreateRequest(
        String brandName,
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
    public static AdminCampaignCreateRequest convertPublishToCreateRequest(AdminCampaignCreateRequest publishRequest) {
        return new AdminCampaignCreateRequest(
                publishRequest.brandName,
                publishRequest.campaignTitle(),
                publishRequest.language(),
                publishRequest.campaignType(),
                publishRequest.campaignProductType(),
                safeList(publishRequest.thumbnailImages()),
                safeList(publishRequest.detailImages()),
                publishRequest.applyStartDate(),
                publishRequest.applyDeadline(),
                publishRequest.creatorAnnouncementDate(),
                publishRequest.reviewSubmissionDeadline(),
                publishRequest.recruitmentNumber(),
                safeList(publishRequest.participationRewards()),
                safeList(publishRequest.deliverableRequirements()),
                safeList(publishRequest.eligibilityRequirements()),
                publishRequest.firstContentType(),
                publishRequest.secondContentType()
        );
    }

    private static <T> List<T> safeList(List<T> original) {
        return original != null ? new ArrayList<>(original) : new ArrayList<>();
    }
}
