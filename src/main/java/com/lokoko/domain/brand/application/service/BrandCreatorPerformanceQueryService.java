package com.lokoko.domain.brand.application.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lokoko.domain.brand.api.dto.response.CreatorPerformanceResponse;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewGetService;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ContentStatus;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewStatus;
import com.lokoko.domain.creator.api.dto.response.CreatorInfo;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.media.socialclip.application.service.SocialClipGetService;
import com.lokoko.domain.media.socialclip.domain.SocialClip;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.global.common.response.PageableResponse;
import com.lokoko.global.config.BetaFeatureConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandCreatorPerformanceQueryService {

    private final CreatorCampaignGetService creatorCampaignGetService;
    private final CampaignReviewGetService campaignReviewGetService;
    private final SocialClipGetService socialClipGetService;
    private final BetaFeatureConfig betaFeatureConfig;

    public CreatorPerformanceResponse getCreatorPerformances(Campaign campaign, int page, int size) {
        List<CreatorCampaign> approvedCreatorCampaigns = creatorCampaignGetService.findAllByCampaign(campaign).stream()
                .filter(creatorCampaign -> creatorCampaign.getStatus() != ParticipationStatus.REJECTED)
                .sorted(Comparator.comparing(CreatorCampaign::getAppliedAt))
                .toList();

        List<CreatorPerformanceResponse.CreatorReviewPerformance> allCreatorPerformances =
                approvedCreatorCampaigns.stream()
                        .collect(Collectors.groupingBy(
                                CreatorCampaign::getCreator,
                                LinkedHashMap::new,
                                Collectors.toList()
                        ))
                        .entrySet().stream()
                        .map(creatorCampaignEntry -> {
                            Creator creator = creatorCampaignEntry.getKey();
                            List<CreatorCampaign> creatorCampaignList = creatorCampaignEntry.getValue();

                            List<CreatorPerformanceResponse.ReviewPerformance> reviewPerformances =
                                    buildReviewPerformances(campaign, creatorCampaignList);

                            return CreatorPerformanceResponse.CreatorReviewPerformance.builder()
                                    .creator(CreatorInfo.builder()
                                            .creatorId(creator.getId())
                                            .creatorFullName(creator.getUser().getName())
                                            .creatorNickname(creator.getCreatorName())
                                            .profileImageUrl(creator.getUser().getProfileImageUrl())
                                            .build())
                                    .reviews(reviewPerformances)
                                    .build();
                        })
                        .toList();

        return buildPagedCreatorPerformanceResponse(campaign, allCreatorPerformances, page, size);
    }

    private CreatorPerformanceResponse buildPagedCreatorPerformanceResponse(
            Campaign campaign,
            List<CreatorPerformanceResponse.CreatorReviewPerformance> allCreatorPerformances,
            int page,
            int size
    ) {
        long totalElements = allCreatorPerformances.size();
        long startIndex = (long) page * size;

        if (startIndex >= totalElements) {
            PageableResponse pageableResponse = PageableResponse.of(
                    page,
                    size,
                    0,
                    true,
                    totalElements
            );

            return buildCreatorPerformanceResponse(campaign, List.of(), pageableResponse);
        }

        int safeStartIndex = (int) startIndex;
        int endIndex = (int) Math.min(startIndex + size, totalElements);

        List<CreatorPerformanceResponse.CreatorReviewPerformance> pagedCreatorPerformances =
                allCreatorPerformances.subList(safeStartIndex, endIndex);

        PageableResponse pageableResponse = PageableResponse.of(
                page,
                size,
                pagedCreatorPerformances.size(),
                endIndex >= totalElements,
                totalElements
        );

        return buildCreatorPerformanceResponse(campaign, pagedCreatorPerformances, pageableResponse);
    }

    private CreatorPerformanceResponse buildCreatorPerformanceResponse(
            Campaign campaign,
            List<CreatorPerformanceResponse.CreatorReviewPerformance> creatorPerformances,
            PageableResponse pageableResponse
    ) {
        return CreatorPerformanceResponse.builder()
                .campaignId(campaign.getId())
                .campaignTitle(campaign.getTitle())
                .firstContentPlatform(campaign.getFirstContentPlatform())
                .secondContentPlatform(campaign.getSecondContentPlatform())
                .creators(creatorPerformances)
                .pageableResponse(pageableResponse)
                .build();
    }

    private List<CreatorPerformanceResponse.ReviewPerformance> buildReviewPerformances(
            Campaign campaign,
            List<CreatorCampaign> creatorCampaigns
    ) {
        List<ContentType> contentTypes = new ArrayList<>();
        contentTypes.add(campaign.getFirstContentPlatform());
        if (campaign.getSecondContentPlatform() != null) {
            contentTypes.add(campaign.getSecondContentPlatform());
        }

        List<CreatorPerformanceResponse.ReviewPerformance> performances = new ArrayList<>();

        for (CreatorCampaign creatorCampaign : creatorCampaigns) {
            List<CampaignReview> reviews = campaignReviewGetService.findAllByCreatorCampaignId(creatorCampaign.getId());

            Map<ContentType, CampaignReview> firstReviews = reviews.stream()
                    .filter(review -> review.getReviewRound() == ReviewRound.FIRST)
                    .collect(Collectors.toMap(CampaignReview::getContentType, review -> review, (a, b) -> a));

            Map<ContentType, CampaignReview> secondReviews = reviews.stream()
                    .filter(review -> review.getReviewRound() == ReviewRound.SECOND)
                    .collect(Collectors.toMap(CampaignReview::getContentType, review -> review, (a, b) -> a));

            for (ContentType contentType : contentTypes) {
                CampaignReview secondReview = secondReviews.get(contentType);
                CampaignReview firstReview = firstReviews.get(contentType);

                if (secondReview != null) {
                    performances.add(buildReviewPerformance(secondReview));
                    continue;
                }
                if (firstReview != null) {
                    performances.add(buildReviewPerformance(firstReview));
                    continue;
                }

                ContentStatus contentStatus = Boolean.TRUE.equals(creatorCampaign.getAddressConfirmed())
                        ? ContentStatus.IN_PROGRESS
                        : ContentStatus.NOT_SUBMITTED;

                performances.add(CreatorPerformanceResponse.ReviewPerformance.builder()
                        .reviewRound(ReviewRound.FIRST)
                        .reviewStatus(contentStatus)
                        .contents(CreatorPerformanceResponse.ContentMetrics.builder()
                                .contentType(contentType)
                                .build())
                        .build());
            }
        }

        return performances;
    }

    private CreatorPerformanceResponse.ReviewPerformance buildReviewPerformance(CampaignReview review) {
        ContentStatus contentStatus = getReviewContentStatus(review);
        String postUrl = null;
        Long viewCount = null;
        Long likeCount = null;
        Long commentCount = null;
        Long shareCount = null;
        Instant uploadedAt = null;

        if (review.getReviewRound() == ReviewRound.SECOND && review.getStatus() == ReviewStatus.RESUBMITTED) {
            postUrl = review.getPostUrl();

            Optional<SocialClip> socialClip = socialClipGetService.findByCampaignReview(review);
            if (socialClip.isPresent()) {
                SocialClip clip = socialClip.get();
                viewCount = clip.getPlays();
                likeCount = clip.getLikes();
                commentCount = clip.getComments();
                shareCount = clip.getShares();
                uploadedAt = clip.getUploadedAt();
            }
        } else if (betaFeatureConfig.isFirstReviewUrlEnabled()
                && review.getReviewRound() == ReviewRound.FIRST
                && review.getPostUrl() != null) {
            postUrl = review.getPostUrl();
            uploadedAt = review.getCreatedAt().toInstant(java.time.ZoneOffset.ofHours(9));
        }

        CreatorPerformanceResponse.ContentMetrics contents = null;
        if (review.getContentType() != null) {
            contents = CreatorPerformanceResponse.ContentMetrics.builder()
                    .contentType(review.getContentType())
                    .viewCount(viewCount)
                    .likeCount(likeCount)
                    .commentCount(commentCount)
                    .shareCount(shareCount)
                    .build();
        }

        return CreatorPerformanceResponse.ReviewPerformance.builder()
                .campaignReviewId(review.getId())
                .reviewRound(review.getReviewRound())
                .reviewStatus(contentStatus)
                .postUrl(postUrl)
                .contents(contents)
                .uploadedAt(uploadedAt)
                .build();
    }

    private ContentStatus getReviewContentStatus(CampaignReview review) {
        ReviewStatus status = review.getStatus();

        if (betaFeatureConfig.isSimplifiedReviewFlow()
                && review.getReviewRound() == ReviewRound.FIRST
                && status == ReviewStatus.SUBMITTED) {
            return ContentStatus.FINAL_UPLOADED;
        }

        return switch (status) {
            case SUBMITTED -> ContentStatus.PENDING_REVISION;
            case REVISION_REQUESTED -> review.isNoteViewed()
                    ? ContentStatus.REVISING
                    : ContentStatus.PENDING_REVISION;
            case RESUBMITTED -> ContentStatus.FINAL_UPLOADED;
        };
    }
}
