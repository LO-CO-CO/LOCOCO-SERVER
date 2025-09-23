package com.lokoko.domain.campaign.api.dto.response;

import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignDetailPageStatus;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignLanguage;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

public record CampaignDetailResponse(

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "캠페인 id" , example = "1")
        Long campaignId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "캠페인 종류" , example = "GIVEAWAY")
        CampaignType campaignType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "캠페인 제목"  , example = "캠페인aa")
        String title,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "브랜드 이름" , example = "브랜드A")
        String brandName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "캠페인 지원 시작 날짜" , example = "2025-09-16T07:32:08.995Z")
        Instant applyStartDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "캠페인 지원 마감 날짜", example = "2025-09-16T07:32:08.995Z" )
        Instant applyDeadline,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "크리에이터 발표 날짜" , example = "2025-09-16T07:32:08.995Z")
        Instant creatorAnnouncementDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "2차 리뷰 제출 마감일" , example = "2025-09-16T07:32:08.995Z")
        Instant reviewSubmissionDeadline,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "크리에이터 컨텐츠 제출 요구사항 리스트")
        List<String> deliverableRequirements,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "크리에이터 참요 조건 리스트")
        List<String> participationRewards,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "크리에이터 자격 요건 리스트")
        List<String> eligibilityRequirements,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "상단 이미지 목록 리스트")
        List<CampaignImageResponse> topImages,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "하단 이미지 목록 리스트")
        List<CampaignImageResponse> bottomImages,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "캠페인 상태" , example = "Coming Soon, Apply Now!, Completed ....")
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
                brand.getBrandName(),
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
