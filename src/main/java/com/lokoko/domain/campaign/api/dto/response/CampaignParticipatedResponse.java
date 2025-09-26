package com.lokoko.domain.campaign.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CampaignParticipatedResponse(

        @Schema(description = "참여한 캠페인 ID", example = "11")
        Long campaignId,

        @Schema(requiredMode = REQUIRED, description = "참여한 캠페인 제목", example = "Summer Hydration Campaign")
        String title,

        @Schema(requiredMode = REQUIRED, description = "브랜드가 지정한 1번째 리뷰 컨텐츠 타입(캠페인 설정)", example = "INSTA_REELS")
        ContentType firstContentPlatform,

        @Schema(description = "브랜드가 지정한 2번째 리뷰 컨텐츠 타입(없을 수 있음)", example = "TIKTOK_VIDEO")
        ContentType secondContentPlatform,

        @Schema(requiredMode = REQUIRED, description = "현재 업로드해야 할 리뷰 라운드", example = "FIRST")
        ReviewRound nowReviewRound,

        @Schema(description = "브랜드 노트(있다면 반환)", example = "Please focus on the product's hydrating effects.")
        String brandNote,

        @Schema(description = "브랜드 노트 작성 시간(있다면 반환)", example = "2023-10-05T14:48:00Z")
        Instant revisionRequestedAt
) {
}
