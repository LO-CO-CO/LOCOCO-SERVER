package com.lokoko.domain.creator.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreatorMyCampaignResponse(

        @Schema(description = "캠페인 ID")
        Long campaignId,

        @Schema(description = "캠페인 이름")
        String title,

        @Schema(description = "리뷰 제출 데드라인")
        Instant reviewSubmissionDeadline,

        @Schema(description = "참여 상태", example = "APPROVED_ADDRESS_CONFIRMED")
        ParticipationStatus participationStatus
) {
}
