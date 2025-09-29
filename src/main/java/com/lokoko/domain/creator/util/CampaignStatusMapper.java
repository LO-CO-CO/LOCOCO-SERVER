package com.lokoko.domain.creator.util;

import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewStatus;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creator.api.dto.response.NextAction;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

/**
 * 개선된 ParticipationStatus를 사용한 간소화된 상태 매퍼
 * 이제 ParticipationStatus가 클라이언트 형식을 직접 제공하므로 매핑 로직이 훨씬 간단해짐
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CampaignStatusMapper {


    /**
     * ParticipationStatus를 직접 받는 메서드
     */
    public static NextAction determineNextAction(
            ParticipationStatus participationStatus,
            List<ReviewInfo> reviews,
            List<ContentType> requiredContentTypes) {

        if (participationStatus == ParticipationStatus.ACTIVE) {
            return determineActiveAction(reviews, requiredContentTypes);
        }

        return mapToNextAction(participationStatus.getDefaultAction());
    }

    /**
     * Active 상태에서의 세부 액션 결정
     */
    private static NextAction determineActiveAction(List<ReviewInfo> reviews, List<ContentType> requiredContentTypes) {
        if (reviews.isEmpty()) {
            return NextAction.UPLOAD_FIRST_REVIEW;
        }

        // FIRST 라운드에서 SUBMITTED 상태 확인 (브랜드 승인 대기) - 최우선
        boolean hasFirstSubmitted = reviews.stream()
                .anyMatch(review -> review.reviewRound() == ReviewRound.FIRST
                    && review.reviewStatus() == ReviewStatus.SUBMITTED);

        if (hasFirstSubmitted) {
            return NextAction.REVISION_REQUESTED;
        }

        // 수정 요청된 리뷰가 있는지 확인
        boolean hasRevisionRequested = reviews.stream()
                .anyMatch(review -> review.reviewStatus() == ReviewStatus.REVISION_REQUESTED);

        if (hasRevisionRequested) {
            // 브랜드 노트를 확인했는지 여부에 따라 다른 액션 반환
            boolean allNotesViewed = reviews.stream()
                    .filter(review -> review.reviewStatus() == ReviewStatus.REVISION_REQUESTED)
                    .allMatch(ReviewInfo::noteViewed);

            return allNotesViewed ? NextAction.UPLOAD_SECOND_REVIEW : NextAction.VIEW_NOTES;
        }

        // FIRST 라운드가 완료되었고 SUBMITTED가 아닌 경우 (브랜드 승인 후)
        boolean hasFirstReview = reviews.stream()
                .anyMatch(review -> review.reviewRound() == ReviewRound.FIRST);

        if (hasFirstReview) {
            return NextAction.UPLOAD_SECOND_REVIEW;
        }

        return NextAction.UPLOAD_FIRST_REVIEW;
    }

    /**
     * String 액션을 NextAction enum으로 매핑
     */
    private static NextAction mapToNextAction(String action) {
        return switch (action) {
            case "View Details" -> NextAction.VIEW_DETAILS;
            case "Confirm Address" -> NextAction.CONFIRM_ADDRESS;
            case "Upload 1st Review" -> NextAction.UPLOAD_FIRST_REVIEW;
            case "Upload 2nd Review" -> NextAction.UPLOAD_SECOND_REVIEW;
            case "View Results" -> NextAction.VIEW_RESULTS;
            default -> NextAction.VIEW_DETAILS;
        };
    }




    /**
     * 리뷰 정보를 담는 record
     */
    public record ReviewInfo(
            Long campaignReviewId,
            com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound reviewRound,
            ContentType contentType,
            ReviewStatus reviewStatus,
            java.time.Instant reviewUploadedAt,
            boolean noteViewed
    ) {}
}