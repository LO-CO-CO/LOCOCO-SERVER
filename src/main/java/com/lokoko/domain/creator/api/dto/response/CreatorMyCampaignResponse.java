package com.lokoko.domain.creator.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.creator.util.CampaignStatusMapper;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
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

        @Schema(description = "다음 액션", example = "Upload 1st Review", allowableValues = {"View Details", "Confirm Address", "Upload 1st Review", "Revision Requested", "View Notes", "Upload 2nd Review", "View Results"})
        String nextAction,


        @Schema(description = "참여 상태 (내부용)", example = "APPROVED_ADDRESS_CONFIRMED")
        @Deprecated
        ParticipationStatus participationStatus
) {

    /**
     * 개선된 ParticipationStatus를 사용한 간소화된 factory method
     */
    public static CreatorMyCampaignResponse of(
            Long campaignId,
            String title,
            Instant reviewSubmissionDeadline,
            ParticipationStatus participationStatus,
            List<CampaignStatusMapper.ReviewInfo> reviews,
            List<ContentType> requiredContentTypes) {

        String nextAction = CampaignStatusMapper.determineNextAction(
                participationStatus, reviews, requiredContentTypes);

        return CreatorMyCampaignResponse.builder()
                .campaignId(campaignId)
                .title(title)
                .reviewSubmissionDeadline(reviewSubmissionDeadline)
                .nextAction(nextAction)
                .participationStatus(participationStatus) // 호환성을 위해 유지
                .build();
    }

    /**
     * 간단한 버전 (리뷰 정보 없이) - 이제 더욱 간단해짐!
     */
    public static CreatorMyCampaignResponse ofSimple(
            Long campaignId,
            String title,
            Instant reviewSubmissionDeadline,
            ParticipationStatus participationStatus) {

        return CreatorMyCampaignResponse.builder()
                .campaignId(campaignId)
                .title(title)
                .reviewSubmissionDeadline(reviewSubmissionDeadline)
                .nextAction("브랜드 승인 대기")  // PENDING 상태의 한국어 액션
                .participationStatus(participationStatus)
                .build();
    }
}
