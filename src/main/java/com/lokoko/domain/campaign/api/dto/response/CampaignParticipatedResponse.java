package com.lokoko.domain.campaign.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CampaignParticipatedResponse(

        @Schema(description = "참여한 캠페인 ID", example = "11")
        Long campaignId,

        @Schema(requiredMode = REQUIRED, description = "참여한 캠페인 제목", example = "Summer Hydration Campaign")
        String title,

        @Schema(description = "컨텐츠 타입별 리뷰 상태 목록")
        List<ReviewContentStatus> reviewContents
) {

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ReviewContentStatus(
            @Schema(description = "컨텐츠 타입", example = "INSTA_REELS")
            ContentType contentType,

            @Schema(description = "현재 업로드해야 할 리뷰 라운드", example = "FIRST")
            ReviewRound nowReviewRound,

            @Schema(description = "브랜드 노트(있다면 반환)", example = "Please focus on the product's hydrating effects.")
            String brandNote,

            @Schema(description = "브랜드 노트 작성 시간(있다면 반환)", example = "2023-10-05T14:48:00Z")
            Instant revisionRequestedAt,

            @Schema(description = "기존 리뷰의 캡션과 해시태그(있다면 반환)", example = "Great product! #sponsored #beauty")
            String captionWithHashtags,

            @Schema(description = "기존 리뷰의 미디어 URL 목록(있다면 반환)")
            List<String> mediaUrls
    ) {}
}
