package com.lokoko.domain.campaign.api.dto.request;

import com.lokoko.domain.campaign.domain.entity.enums.CampaignLanguage;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductType;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignType;
import com.lokoko.domain.socialclip.domain.entity.enums.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.*;

public record CampaignPublishRequest(
        @Schema(requiredMode = REQUIRED, description = "캠페인 제목", example = "로코코 신제품")
        @NotBlank(message = "캠페인 제목은 필수입니다")
        String campaignTitle,

        @Schema(requiredMode = REQUIRED, description = "캠페인 언어 설정", example = "EN 또는 ES")
        @NotNull(message = "언어 설정은 필수입니다")
        CampaignLanguage language,

        @Schema(requiredMode = REQUIRED, description = "캠페인 타입", example = "GIVEAWAY 또는 CONTENTS 또는 EXCLUSIVE")
        @NotNull(message = "캠페인 타입은 필수입니다")
        CampaignType campaignType,

        @Schema(requiredMode = REQUIRED, description = "캠페인 상품 타입", example = "SKINCARE 또는 SUNCARE 또는 MAKEUP")
        @NotNull(message = "캠페인 상품 타입은 필수입니다")
        CampaignProductType campaignProductType,

        @Schema(requiredMode = REQUIRED, description = "썸네일 이미지 목록 (최소 1개, 최대 5개)")
        @NotEmpty(message = "상단 이미지는 최소 1개 이상 필요합니다")
        @Size(max = 5, message = "상단 이미지는 최대 5개까지 가능합니다")
        List<CampaignImageRequest> thumbnailImages,

        @Schema(requiredMode = NOT_REQUIRED, description = "상세 이미지 목록 (최대 15개)")
        @Size(max = 15, message = "하단 이미지는 최대 15개까지 가능합니다")
        List<CampaignImageRequest> detailImages,

        @Schema(requiredMode = REQUIRED, description = "신청 시작일", example = "2024-12-01T00:00:00Z")
        @NotNull(message = "신청 시작일은 필수입니다")
        @Future(message = "신청 시작일은 미래 날짜여야 합니다")
        Instant applyStartDate,

        @Schema(requiredMode = REQUIRED, description = "신청 마감일", example = "2024-12-15T23:59:59Z")
        @NotNull(message = "신청 마감일은 필수입니다")
        @Future(message = "신청 마감일은 미래 날짜여야 합니다")
        Instant applyDeadline,

        @Schema(requiredMode = REQUIRED, description = "크리에이터 발표일", example = "2024-12-20T00:00:00Z")
        @NotNull(message = "크리에이터 발표일은 필수입니다")
        @Future(message = "크리에이터 발표일은 미래 날짜여야 합니다")
        Instant creatorAnnouncementDate,

        @Schema(requiredMode = REQUIRED, description = "리뷰 제출 마감일", example = "2025-01-15T23:59:59Z")
        @NotNull(message = "리뷰 제출 마감일은 필수입니다")
        @Future(message = "리뷰 제출 마감일은 미래 날짜여야 합니다")
        Instant reviewSubmissionDeadline,

        @Schema(requiredMode = REQUIRED, description = "모집 인원 수", example = "10")
        @NotNull(message = "모집 인원은 필수입니다")
        @Min(value = 1, message = "모집 인원은 최소 1명 이상이어야 합니다")
        Integer recruitmentNumber,

        @Schema(requiredMode = REQUIRED, description = "참여 혜택 목록", example = "[\"신제품 무료 제공\", \"배송비 무료\"]")
        @NotEmpty(message = "참여 혜택은 최소 1개 이상 필요합니다")
        List<@NotBlank(message = "참여 혜택 항목은 공백일 수 없습니다") String> participationRewards,

        @Schema(requiredMode = REQUIRED, description = "컨텐츠 요구사항 목록", example = "[\"인스타그램 피드 포스팅\", \"스토리 업로드\"]")
        @NotEmpty(message = "컨텐츠 요구사항은 최소 1개 이상 필요합니다")
        List<@NotBlank(message = "컨텐츠 요구사항 항목은 공백일 수 없습니다") String> deliverableRequirements,

        @Schema(requiredMode = NOT_REQUIRED, description = "참여 자격 요건 목록", example = "[\"팔로워 1000명 이상\", \"뷰티 관심분야\"]")
        List<String> eligibilityRequirements,

        @Schema(requiredMode = REQUIRED, description = "첫 번째 컨텐츠 플랫폼", example = "INSTAGRAM_REELS 또는 INSTAGRAM_POST 또는 TIKTOK_VIDEO")
        @NotNull(message = "컨텐츠 플랫폼 선택은 필수입니다")
        ContentType firstContentType,

        @Schema(requiredMode = REQUIRED, description = "두 번째 컨텐츠 플랫폼", example = "INSTAGRAM_REELS 또는 INSTAGRAM_POST 또는 TIKTOK_VIDEO")
        @NotNull(message = "컨텐츠 플랫폼 선택은 필수입니다")
        ContentType secondContentType
) {
}