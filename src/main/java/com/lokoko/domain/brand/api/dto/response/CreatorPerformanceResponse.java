package com.lokoko.domain.brand.api.dto.response;

import com.lokoko.domain.campaignReview.domain.entity.enums.ContentStatus;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creator.api.dto.response.CreatorInfo;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
@Schema(description = "캠페인별 크리에이터 성과 응답")
public record CreatorPerformanceResponse(
        @Schema(requiredMode = REQUIRED, description = "캠페인 ID", example = "1")
        Long campaignId,

        @Schema(requiredMode = REQUIRED, description = "캠페인 제목", example = "여름 신상 홍보 캠페인")
        String campaignTitle,

        @Schema(requiredMode = REQUIRED, description = "1차 콘텐츠 플랫폼", example = "INSTA_REELS")
        ContentType firstContentPlatform,

        @Schema(requiredMode = NOT_REQUIRED, description = "2차 콘텐츠 플랫폼", example = "TIKTOK_VIDEO")
        ContentType secondContentPlatform,

        @Schema(requiredMode = REQUIRED, description = "크리에이터별 리뷰 성과 목록")
        List<CreatorReviewPerformance> creators,

        @Schema(requiredMode = REQUIRED, description = "페이지네이션 정보")
        PageableResponse pageableResponse
) {
    @Builder
    @Schema(description = "크리에이터별 리뷰 성과")
    public record CreatorReviewPerformance(
            @Schema(requiredMode = REQUIRED, description = "크리에이터 정보")
            CreatorInfo creator,

            @Schema(requiredMode = REQUIRED, description = "리뷰 성과 목록")
            List<ReviewPerformance> reviews
    ) {
    }

    @Builder
    @Schema(description = "리뷰 성과 정보")
    public record ReviewPerformance(
            @Schema(requiredMode = NOT_REQUIRED, description = "캠페인 리뷰 ID", example = "100")
            Long campaignReviewId,

            @Schema(requiredMode = REQUIRED, description = "리뷰 라운드", example = "SECOND")
            ReviewRound reviewRound,

            @Schema(requiredMode = REQUIRED, description = "콘텐츠 타입", example = "TIKTOK_VIDEO")
            ContentType contentType,

            @Schema(requiredMode = REQUIRED, description = "리뷰 상태", example = "FINAL_UPLOADED")
            ContentStatus reviewStatus,

            @Schema(requiredMode = NOT_REQUIRED, description = "게시물 URL (2차 리뷰만)", example = "https://www.instagram.com/p/ABC123/")
            String postUrl,

            @Schema(requiredMode = NOT_REQUIRED, description = "조회수 (2차 리뷰만)", example = "15000")
            Long viewCount,

            @Schema(requiredMode = NOT_REQUIRED, description = "좋아요 수 (2차 리뷰만)", example = "1200")
            Long likeCount,

            @Schema(requiredMode = NOT_REQUIRED, description = "댓글 수 (2차 리뷰만)", example = "85")
            Long commentCount,

            @Schema(requiredMode = NOT_REQUIRED, description = "공유 수 (2차 리뷰만)", example = "30")
            Long shareCount,

            @Schema(requiredMode = NOT_REQUIRED, description = "업로드 시간 (2차 리뷰만)", example = "2024-01-15T10:30:00Z")
            Instant uploadedAt
    ) {
    }
}
