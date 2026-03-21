package com.lokoko.domain.campaignReview.application.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lokoko.domain.campaign.api.dto.response.CampaignParticipatedResponse;
import com.lokoko.domain.campaign.application.mapper.CampaignMapper;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaignReview.api.dto.response.CompletedReviewResponse;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.creatorCampaign.exception.CampaignReviewAbleNotFoundException;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.global.config.BetaFeatureConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignReviewReadService {

    private final CampaignReviewGetService campaignReviewGetService;
    private final CampaignGetService campaignGetService;
    private final CreatorCampaignGetService creatorCampaignGetService;
    private final CampaignMapper campaignMapper;
    private final BetaFeatureConfig betaFeatureConfig;

    @Transactional
    public CampaignParticipatedResponse getMyReviewableCampaign(Long creatorId, Long campaignId, ReviewRound round) {
        CreatorCampaign creatorCampaign = findReviewableCampaign(creatorId, campaignId);

        markBrandNotesAsViewed(creatorCampaign);

        List<CampaignParticipatedResponse.ReviewContentStatus> reviewContents = round != null
                ? createRoundSpecificReviewContentStatuses(creatorCampaign, round)
                : createReviewContentStatuses(creatorCampaign);

        return campaignMapper.toCampaignParticipationResponse(creatorCampaign, reviewContents);
    }

    @Transactional(readOnly = true)
    public List<CampaignParticipatedResponse> getMyReviewables(Long creatorId, ReviewRound round) {
        List<CreatorCampaign> eligibles = creatorCampaignGetService.findReviewable(creatorId);

        return eligibles.stream()
                .filter(creatorCampaign -> creatorCampaign.getStatus() == ParticipationStatus.ACTIVE)
                .map(creatorCampaign -> {
                    List<CampaignParticipatedResponse.ReviewContentStatus> reviewContents = round != null
                            ? createRoundSpecificReviewContentStatuses(creatorCampaign, round)
                            : createReviewContentStatuses(creatorCampaign);

                    return campaignMapper.toCampaignParticipationResponse(creatorCampaign, reviewContents);
                })
                .filter(response -> !response.reviewContents().isEmpty())
                .toList();
    }

    @Transactional(readOnly = true)
    public CompletedReviewResponse getCompletedReviews(Long creatorId, Long campaignId) {
        Campaign campaign = campaignGetService.findByCampaignId(campaignId);
        CreatorCampaign creatorCampaign = creatorCampaignGetService.getByCampaignAndCreatorId(campaign, creatorId);

        if (creatorCampaign.getStatus() != ParticipationStatus.COMPLETED) {
            throw new CampaignReviewAbleNotFoundException();
        }

        List<CompletedReviewResponse.CompletedReviewContent> reviewContents =
                betaFeatureConfig.isSimplifiedReviewFlow()
                        ? createCompletedFirstReviewContents(creatorCampaign)
                        : createCompletedReviewContents(creatorCampaign);

        return CompletedReviewResponse.builder()
                .campaignId(campaignId)
                .campaignName(campaign.getTitle())
                .reviewContents(reviewContents)
                .build();
    }

    private CreatorCampaign findReviewableCampaign(Long creatorId, Long campaignId) {
        CreatorCampaign creatorCampaign;
        if (betaFeatureConfig.isSimplifiedReviewFlow()) {
            creatorCampaign = creatorCampaignGetService.findReviewableInMultipleStatusesForBeta(creatorId, campaignId);
        } else {
            creatorCampaign = creatorCampaignGetService.findReviewableInReviewByCampaign(creatorId, campaignId);
        }

        if (creatorCampaign.getStatus() != ParticipationStatus.ACTIVE) {
            throw new CampaignReviewAbleNotFoundException();
        }
        return creatorCampaign;
    }

    private List<CampaignParticipatedResponse.ReviewContentStatus> createReviewContentStatuses(
            CreatorCampaign creatorCampaign
    ) {
        Campaign campaign = creatorCampaign.getCampaign();
        List<ContentType> campaignContentTypes = getCampaignContentTypes(campaign);

        List<ContentType> existingFirstTypes =
                campaignReviewGetService.findContentTypesByRound(creatorCampaign.getId(), ReviewRound.FIRST);
        List<ContentType> existingSecondTypes =
                campaignReviewGetService.findContentTypesByRound(creatorCampaign.getId(), ReviewRound.SECOND);

        return campaignContentTypes.stream()
                .map(contentType -> {
                    boolean hasFirstReview = existingFirstTypes.contains(contentType);
                    ReviewRound currentRound = hasFirstReview ? ReviewRound.SECOND : ReviewRound.FIRST;

                    String brandNote = null;
                    Instant revisionRequestedAt = null;
                    if (currentRound == ReviewRound.SECOND) {
                        Optional<CampaignReview> firstReviewForContentType = campaignReviewGetService
                                .findByContentType(creatorCampaign.getId(), ReviewRound.FIRST, contentType);
                        if (firstReviewForContentType.isPresent()) {
                            CampaignReview review = firstReviewForContentType.get();
                            brandNote = review.getBrandNote();
                            revisionRequestedAt = review.getRevisionRequestedAt();
                        }
                    }

                    String captionWithHashtags = null;
                    List<String> mediaUrls = null;
                    if (hasFirstReview) {
                        Optional<CampaignReview> existingReview = campaignReviewGetService
                                .findByContentType(creatorCampaign.getId(), ReviewRound.FIRST, contentType);
                        if (existingReview.isPresent()) {
                            CampaignReview review = existingReview.get();
                            captionWithHashtags = review.getCaptionWithHashtags();
                            mediaUrls = campaignReviewGetService.getOrderedMediaUrls(review);
                        }
                    }

                    return campaignMapper.toReviewContentStatus(
                            contentType,
                            currentRound,
                            brandNote,
                            revisionRequestedAt,
                            captionWithHashtags,
                            mediaUrls
                    );
                })
                .filter(status -> !existingSecondTypes.contains(status.contentType()))
                .toList();
    }

    private List<CampaignParticipatedResponse.ReviewContentStatus> createRoundSpecificReviewContentStatuses(
            CreatorCampaign creatorCampaign,
            ReviewRound targetRound
    ) {
        Campaign campaign = creatorCampaign.getCampaign();
        List<ContentType> campaignContentTypes = getCampaignContentTypes(campaign);

        List<ContentType> existingFirstTypes =
                campaignReviewGetService.findContentTypesByRound(creatorCampaign.getId(), ReviewRound.FIRST);
        List<ContentType> existingSecondTypes =
                campaignReviewGetService.findContentTypesByRound(creatorCampaign.getId(), ReviewRound.SECOND);

        return campaignContentTypes.stream()
                .filter(contentType -> {
                    boolean hasFirstReview = existingFirstTypes.contains(contentType);
                    boolean hasSecondReview = existingSecondTypes.contains(contentType);
                    return targetRound == ReviewRound.FIRST
                            ? !hasFirstReview
                            : hasFirstReview && !hasSecondReview;
                })
                .map(contentType -> {
                    String brandNote = null;
                    Instant revisionRequestedAt = null;
                    if (targetRound == ReviewRound.SECOND) {
                        Optional<CampaignReview> firstReviewForContentType = campaignReviewGetService
                                .findByContentType(creatorCampaign.getId(), ReviewRound.FIRST, contentType);
                        if (firstReviewForContentType.isPresent()) {
                            CampaignReview review = firstReviewForContentType.get();
                            brandNote = review.getBrandNote();
                            revisionRequestedAt = review.getRevisionRequestedAt();
                        }
                    }

                    String captionWithHashtags = null;
                    List<String> mediaUrls = null;
                    if (targetRound == ReviewRound.SECOND) {
                        Optional<CampaignReview> firstReviewForContentType = campaignReviewGetService
                                .findByContentType(creatorCampaign.getId(), ReviewRound.FIRST, contentType);
                        if (firstReviewForContentType.isPresent()) {
                            CampaignReview review = firstReviewForContentType.get();
                            captionWithHashtags = review.getCaptionWithHashtags();
                            mediaUrls = campaignReviewGetService.getOrderedMediaUrls(review);
                        }
                    }

                    return campaignMapper.toReviewContentStatus(
                            contentType,
                            targetRound,
                            brandNote,
                            revisionRequestedAt,
                            captionWithHashtags,
                            mediaUrls
                    );
                })
                .toList();
    }

    private List<CompletedReviewResponse.CompletedReviewContent> createCompletedReviewContents(
            CreatorCampaign creatorCampaign
    ) {
        List<ContentType> existingSecondTypes =
                campaignReviewGetService.findContentTypesByRound(creatorCampaign.getId(), ReviewRound.SECOND);

        return existingSecondTypes.stream()
                .map(contentType -> {
                    Optional<CampaignReview> secondReview = campaignReviewGetService
                            .findByContentType(creatorCampaign.getId(), ReviewRound.SECOND, contentType);

                    if (secondReview.isPresent()) {
                        CampaignReview review = secondReview.get();
                        return CompletedReviewResponse.CompletedReviewContent.builder()
                                .contentType(contentType)
                                .captionWithHashtags(review.getCaptionWithHashtags())
                                .mediaUrls(campaignReviewGetService.getOrderedMediaUrls(review))
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private List<CompletedReviewResponse.CompletedReviewContent> createCompletedFirstReviewContents(
            CreatorCampaign creatorCampaign
    ) {
        List<ContentType> existingFirstTypes =
                campaignReviewGetService.findContentTypesByRound(creatorCampaign.getId(), ReviewRound.FIRST);

        return existingFirstTypes.stream()
                .map(contentType -> {
                    Optional<CampaignReview> firstReview = campaignReviewGetService
                            .findByContentType(creatorCampaign.getId(), ReviewRound.FIRST, contentType);

                    if (firstReview.isPresent()) {
                        CampaignReview review = firstReview.get();
                        return CompletedReviewResponse.CompletedReviewContent.builder()
                                .contentType(contentType)
                                .captionWithHashtags(review.getCaptionWithHashtags())
                                .mediaUrls(campaignReviewGetService.getOrderedMediaUrls(review))
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private void markBrandNotesAsViewed(CreatorCampaign creatorCampaign) {
        List<CampaignReview> firstReviews =
                campaignReviewGetService.getAllByCreatorCampaignAndRound(creatorCampaign, ReviewRound.FIRST);

        firstReviews.stream()
                .filter(review -> review.getBrandNote() != null && !review.getBrandNote().isEmpty())
                .filter(review -> !review.isNoteViewed())
                .forEach(CampaignReview::markNoteAsViewed);
    }

    private List<ContentType> getCampaignContentTypes(Campaign campaign) {
        List<ContentType> contentTypes = new ArrayList<>();
        if (campaign.getFirstContentPlatform() != null) {
            contentTypes.add(campaign.getFirstContentPlatform());
        }
        if (campaign.getSecondContentPlatform() != null) {
            contentTypes.add(campaign.getSecondContentPlatform());
        }
        return contentTypes;
    }
}
