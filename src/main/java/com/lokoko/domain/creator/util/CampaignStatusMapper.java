package com.lokoko.domain.creator.util;

import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewStatus;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
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
    public static String determineNextAction(
            ParticipationStatus participationStatus,
            List<ReviewInfo> reviews,
            List<ContentType> requiredContentTypes) {

        if (participationStatus == ParticipationStatus.ACTIVE) {
            return determineActiveAction(reviews, requiredContentTypes);
        }

        return participationStatus.getDefaultAction();
    }

    /**
     * Active 상태에서의 세부 액션 결정
     */
    private static String determineActiveAction(List<ReviewInfo> reviews, List<ContentType> requiredContentTypes) {
        if (reviews.isEmpty()) {
            return "Upload 1st Review";
        }

        // FIRST 라운드에서 SUBMITTED 상태 확인 (브랜드 승인 대기) - 최우선
        boolean hasFirstSubmitted = reviews.stream()
                .anyMatch(review -> review.reviewRound() == ReviewRound.FIRST
                    && review.reviewStatus() == ReviewStatus.SUBMITTED);

        if (hasFirstSubmitted) {
            return "Revision Requested";
        }

        // 수정 요청된 리뷰가 있는지 확인
        boolean hasRevisionRequested = reviews.stream()
                .anyMatch(review -> review.reviewStatus() == ReviewStatus.REVISION_REQUESTED);

        if (hasRevisionRequested) {
            // 브랜드 노트를 확인했는지 여부에 따라 다른 액션 반환
            boolean allNotesViewed = reviews.stream()
                    .filter(review -> review.reviewStatus() == ReviewStatus.REVISION_REQUESTED)
                    .allMatch(ReviewInfo::noteViewed);

            return allNotesViewed ? "Upload 2nd Review" : "View Notes";
        }

        // FIRST 라운드가 완료되었고 SUBMITTED가 아닌 경우 (브랜드 승인 후)
        boolean hasFirstReview = reviews.stream()
                .anyMatch(review -> review.reviewRound() == ReviewRound.FIRST);

        if (hasFirstReview) {
            return "Upload 2nd Review";
        }

        return "Upload 1st Review";
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