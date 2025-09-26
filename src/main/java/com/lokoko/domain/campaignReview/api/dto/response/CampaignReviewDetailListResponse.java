package com.lokoko.domain.campaignReview.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CampaignReviewDetailListResponse(

        @Schema(requiredMode = REQUIRED, description = "캠페인 ID", example = "11")
        Long campaignId,

        @Schema(requiredMode = REQUIRED, description = "캠페인 제목", example = "Summer Hydration Campaign")
        String title,

        @Schema(requiredMode = REQUIRED, description = "조회한 리뷰 라운드 (몇차 리뷰인지)", example = "FIRST")
        ReviewRound reviewRound,

        @Schema(requiredMode = REQUIRED, description = "해당 라운드의 리뷰 목록 (최신순)")
        List<CampaignReviewDetailResponse> reviews
) {
}
