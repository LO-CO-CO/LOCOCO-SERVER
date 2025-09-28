package com.lokoko.domain.campaignReview.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CampaignReviewDetailListResponse(

        @Schema(requiredMode = REQUIRED, description = "캠페인 ID", example = "11")
        Long campaignId,

        @Schema(requiredMode = REQUIRED, description = "캠페인 제목", example = "Summer Hydration Campaign")
        String title,

        @Schema(requiredMode = REQUIRED, description = "조회한 리뷰 라운드 (몇차 리뷰인지)", example = "FIRST")
        ReviewRound reviewRound,

        @Schema(requiredMode = REQUIRED, description = "콘텐츠 플랫폼", example = "INSTA_REELS")
        ContentType contentType,

        @Schema(requiredMode = REQUIRED, description = "크리에이터가 업로드한 리뷰 이미지 URL 리스트")
        List<String> reviewImages,

        @Schema(requiredMode = REQUIRED, description = "크리에이터가 작성한 캡션 및 해시태그")
        String captionWithHashtags,

        @Schema(description = "브랜드 노트 내용")
        String brandNote,

        @Schema(requiredMode = REQUIRED, description = "브랜드 노트 검수 마감일")
        Instant brandNoteDeadline
) {
}
