package com.lokoko.domain.brand.api.dto.response;

import com.lokoko.domain.campaignReview.domain.entity.enums.ContentStatus;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.global.common.response.PageableResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record CreatorPerformanceResponse(
        Long campaignId,
        String campaignTitle,
        ContentType firstContentPlatform,
        ContentType secondContentPlatform,
        List<CreatorReviewPerformance> creators,
        PageableResponse pageableResponse
) {
    @Builder
    public record CreatorReviewPerformance(
            CreatorInfo creator,
            List<ReviewPerformance> reviews
    ) {
    }

    @Builder
    public record CreatorInfo(
            Long creatorId,
            String creatorFullName,
            String creatorNickname,
            String profileImageUrl
    ) {
    }

    @Builder
    public record ReviewPerformance(
            Long campaignReviewId,
            ReviewRound reviewRound,
            ContentType contentType,
            ContentStatus reviewStatus,
            String postUrl,
            Long viewCount,
            Long likeCount,
            Long commentCount,
            Long shareCount
    ) {
    }
}
