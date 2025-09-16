package com.lokoko.domain.creator.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.socialclip.domain.entity.enums.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreatorMyCampaignResponse(

        @Schema(requiredMode = REQUIRED, description = "크리에이터 기본 정보")
        CreatorBasicInfo basicInfo,

        @Schema(description = "캠페인 ID")
        Long campaignId,

        @Schema(description = "캠페인 이름")
        String title,

        @Schema(description = "리뷰 제출 데드라인")
        Instant reviewSubmissionDeadline,

        @Schema(description = "소셜 클립 콘텐츠 종류", example = "INSTA_REELS")
        ContentType contentType,

        @Schema(description = "참여 상태", example = "APPROVED_ADDRESS_CONFIRMED")
        ParticipationStatus participationStatus
) {
}
