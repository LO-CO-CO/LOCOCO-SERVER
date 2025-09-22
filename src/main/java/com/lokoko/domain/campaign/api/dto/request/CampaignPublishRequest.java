package com.lokoko.domain.campaign.api.dto.request;

import com.lokoko.domain.campaign.domain.entity.enums.CampaignLanguage;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductType;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignType;
import com.lokoko.domain.socialclip.domain.entity.enums.ContentType;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.List;

public record CampaignPublishRequest(
        @NotBlank(message = "캠페인 제목은 필수입니다")
        String campaignTitle,
        
        @NotNull(message = "언어 설정은 필수입니다")
        CampaignLanguage language,
        
        @NotNull(message = "캠페인 타입은 필수입니다")
        CampaignType campaignType,
        
        @NotNull(message = "캠페인 상품 타입은 필수입니다")
        CampaignProductType campaignProductType,
        
        @NotEmpty(message = "상단 이미지는 최소 1개 이상 필요합니다")
        @Size(max = 5, message = "상단 이미지는 최대 5개까지 가능합니다")
        List<CampaignImageRequest> thumbnailImages,
        
        @Size(max = 15, message = "하단 이미지는 최대 15개까지 가능합니다")
        List<CampaignImageRequest> detailImages,
        
        @NotNull(message = "신청 시작일은 필수입니다")
        @Future(message = "신청 시작일은 미래 날짜여야 합니다")
        Instant applyStartDate,
        
        @NotNull(message = "신청 마감일은 필수입니다")
        @Future(message = "신청 마감일은 미래 날짜여야 합니다")
        Instant applyDeadline,
        
        @NotNull(message = "크리에이터 발표일은 필수입니다")
        @Future(message = "크리에이터 발표일은 미래 날짜여야 합니다")
        Instant creatorAnnouncementDate,
        
        @NotNull(message = "리뷰 제출 마감일은 필수입니다")
        @Future(message = "리뷰 제출 마감일은 미래 날짜여야 합니다")
        Instant reviewSubmissionDeadline,
        
        @NotNull(message = "모집 인원은 필수입니다")
        @Min(value = 1, message = "모집 인원은 최소 1명 이상이어야 합니다")
        Integer recruitmentNumber,
        
        @NotEmpty(message = "참여 혜택은 최소 1개 이상 필요합니다")
        List<@NotBlank(message = "참여 혜택 항목은 공백일 수 없습니다") String> participationRewards,
        
        @NotEmpty(message = "컨텐츠 요구사항은 최소 1개 이상 필요합니다")
        List<@NotBlank(message = "컨텐츠 요구사항 항목은 공백일 수 없습니다") String> deliverableRequirements,
        
        // 선택사항
        List<String> eligibilityRequirements,

        @NotNull(message = "컨텐츠 플랫폼 선택은 필수입니다")
        ContentType firstContentType,

        @NotNull(message = "컨텐츠 플랫폼 선택은 필수입니다")
        ContentType secondContentType
) {
}