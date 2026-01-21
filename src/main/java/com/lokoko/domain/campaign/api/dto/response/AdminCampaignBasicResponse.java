package com.lokoko.domain.campaign.api.dto.response;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignLanguage;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductType;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignType;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record AdminCampaignBasicResponse(

        @Schema(requiredMode = REQUIRED, description = "브랜드 이름", example = "LOCOCO")
        String brandName,

        @Schema(requiredMode = REQUIRED, description = "캠페인 id", example = "1")
        Long campaignId,

        @Schema(requiredMode = REQUIRED, description = "캠페인 제목", example = "나는야 캠페인")
        String campaignTitle,

        @Schema(requiredMode = REQUIRED, description = "캠페인 진행 언어", example = "ENG")
        CampaignLanguage language,

        @Schema(requiredMode = REQUIRED, description = "캠페인 종류", example = "GIVEAWAY")
        CampaignType campaignType,

        @Schema(requiredMode = REQUIRED, description = "캠페인 상품 카테고리", example = "SKINCARE")
        CampaignProductType campaignProductType,

        @Schema(requiredMode = REQUIRED, description = "상단 이미지 리스트")
        List<CampaignImageResponse> thumbnailImages,

        @Schema(requiredMode = REQUIRED, description = "하단 이미지 리스트")
        List<CampaignImageResponse> detailImages,

        @Schema(requiredMode = REQUIRED, description = "크리에이터 지원 시작 일시", example = "2025-09-17T시7:32:08.995Z")
        Instant applyStartDate,

        @Schema(requiredMode = REQUIRED, description = "크리에이터 지원 마감 일", example = "2025-09-17T시7:32:08.995Z")
        Instant applyDeadline,

        @Schema(requiredMode = REQUIRED, description = "크리에이터 발표 일시", example = "2025-09-17T07:32:08.995Z")
        Instant creatorAnnouncementDate,

        @Schema(requiredMode = REQUIRED, description = "리뷰 제출 마감일", example = "2025-09-17T07:32:08.995Z")
        Instant reviewSubmissionDeadline,

        @Schema(requiredMode = REQUIRED, description = "모집 인원 수 ", example = "20")
        Integer recruitmentNumber,

        @Schema(requiredMode = REQUIRED, description = "캠페인 참여 보상 리스트")
        List<String> participationRewards,

        @Schema(requiredMode = REQUIRED, description = "컨텐츠 제출 요구사항 리스트")
        List<String> deliverableRequirements,

        @Schema(requiredMode = REQUIRED, description = "크리에이터 자격 요건 리스트")
        List<String> eligibilityRequirements,

        @Schema(requiredMode = REQUIRED, description = "첫 번째 제출 컨텐츠", example = "INSTA_REELS")
        ContentType firstContentType,

        @Schema(requiredMode = REQUIRED, description = "두 번째 제출 컨텐츠", example = "TIKTOK_VIDEO")
        ContentType secondContentType
) {

    public static AdminCampaignBasicResponse of(Campaign campaign, List<CampaignImageResponse> thumbnailImages,
                                           List<CampaignImageResponse> detailImages) {
        return new AdminCampaignBasicResponse(
                campaign.getBrandName(),
                campaign.getId(),
                campaign.getTitle(),
                campaign.getLanguage(),
                campaign.getCampaignType(),
                campaign.getCampaignProductType(),
                thumbnailImages,
                detailImages,
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
