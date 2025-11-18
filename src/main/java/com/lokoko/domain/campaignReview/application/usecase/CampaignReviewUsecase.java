package com.lokoko.domain.campaignReview.application.usecase;


import com.lokoko.domain.campaign.api.dto.response.CampaignParticipatedResponse;
import com.lokoko.domain.campaign.application.mapper.CampaignMapper;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaignReview.api.dto.request.FirstReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.request.SecondReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.response.ReviewUploadResponse;
import com.lokoko.domain.campaignReview.api.dto.response.CompletedReviewResponse;
import com.lokoko.domain.campaignReview.application.mapper.CampaignReviewMapper;
import com.lokoko.global.config.BetaFeatureConfig;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewGetService;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewSaveService;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewStatusManager;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewUpdateService;
import com.lokoko.domain.campaignReview.application.service.CreatorCampaignUpdateService;
import com.lokoko.domain.media.socialclip.application.service.SocialClipSaveService;
import com.lokoko.domain.campaignReview.application.utils.CampaignReviewValidationUtil;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creatorCampaign.exception.CampaignReviewAbleNotFoundException;
import com.lokoko.domain.creator.application.service.CreatorGetService;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.media.api.dto.request.MediaPresignedUrlRequest;
import com.lokoko.domain.media.api.dto.response.MediaPresignedUrlResponse;
import com.lokoko.domain.media.application.utils.MediaValidationUtil;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignReviewUsecase {

    private final CreatorGetService creatorGetService;
    private final CampaignReviewGetService campaignReviewGetService;
    private final CampaignGetService campaignGetService;
    private final CreatorCampaignGetService creatorCampaignGetService;

    private final CampaignReviewSaveService campaignReviewSaveService;
    private final CreatorCampaignUpdateService creatorCampaignUpdateService;
    private final CampaignReviewUpdateService campaignReviewUpdateService;
    private final SocialClipSaveService socialClipSaveService;

    private final CampaignReviewStatusManager campaignReviewStatusManager;
    private final BetaFeatureConfig betaFeatureConfig;

    private final CampaignReviewMapper campaignReviewMapper;
    private final CampaignMapper campaignMapper;

    /**
     * 1차 리뷰 업로드 - 타입은 Campaign.firstContentPlatform / secondContentPlatform 사용 - 두개 리뷰 컨텐츠를 입력 받아야하는 캠페인이면 2세트 모두 필수
     * 아니면 첫세트만 허용 - 동일 타입의 FIRST가 이미 존재하면 409
     */
    @Transactional
    public ReviewUploadResponse uploadFirst(Long userId, Long campaignId, FirstReviewUploadRequest request) {
        Creator creator = creatorGetService.findByUserId(userId);
        Campaign campaign = campaignGetService.findByCampaignId(campaignId);
        CreatorCampaign participation =
                creatorCampaignGetService.getByCampaignAndCreatorId(campaign, creator.getId());

        ContentType typeA = campaign.getFirstContentPlatform();
        ContentType typeB = campaign.getSecondContentPlatform();
        CampaignReviewValidationUtil.validateTwoSetCombination(typeA, typeB);

        // A 세트(캠페인에 second가 없는 단일 타입 캠페인)
        CampaignReviewValidationUtil.requireFirstSetPresent(request.firstMediaUrls(),
                request.firstCaptionWithHashtags());
        MediaValidationUtil.validateTotalMediaCount(request.firstMediaUrls());

        // B 세트(캠페인에 second가 있으면 필수, 없으면 금지)
        if (typeB != null) {
            CampaignReviewValidationUtil.requireFirstSetPresent(
                    request.secondMediaUrls(), request.secondCaptionWithHashtags());
            MediaValidationUtil.validateTotalMediaCount(request.secondMediaUrls());
        } else {
            CampaignReviewValidationUtil.ensureSecondSetAbsentForFirstRound(
                    request.secondMediaUrls(), request.secondCaptionWithHashtags());
        }

        // 미디어 합산 개수 제한
        CampaignReviewValidationUtil.validateCombinedMediaLimit(
                request.firstMediaUrls(),
                (typeB != null) ? request.secondMediaUrls() : null
        );

        // 저장 A (베타 모드일 경우 postUrl 포함)
        CampaignReview firstA;
        if (betaFeatureConfig.isFirstReviewUrlEnabled() && request.firstPostUrl() != null) {
            firstA = campaignReviewMapper.toFirstReview(
                    participation, typeA, request.firstCaptionWithHashtags(), request.firstPostUrl());
        } else {
            firstA = campaignReviewMapper.toFirstReview(
                    participation, typeA, request.firstCaptionWithHashtags());
        }
        CampaignReview savedA = campaignReviewSaveService.saveReview(firstA);
        campaignReviewSaveService.saveMedia(savedA, request.firstMediaUrls());

        // 저장 B(옵션)
        if (typeB != null) {
            CampaignReview firstB;
            if (betaFeatureConfig.isFirstReviewUrlEnabled() && request.secondPostUrl() != null) {
                firstB = campaignReviewMapper.toFirstReview(
                        participation, typeB, request.secondCaptionWithHashtags(), request.secondPostUrl());
            } else {
                firstB = campaignReviewMapper.toFirstReview(
                        participation, typeB, request.secondCaptionWithHashtags());
            }
            CampaignReview savedB = campaignReviewSaveService.saveReview(firstB);
            campaignReviewSaveService.saveMedia(savedB, request.secondMediaUrls());
        }

        creatorCampaignUpdateService.refreshParticipationStatus(participation.getId());
        return campaignReviewMapper.toUploadResponse(savedA);
    }


    /**
     * 2차 리뷰 업로드 - 타입은 Campaign.firstContentPlatform / secondContentPlatform 사용
     * <p> - 두 리뷰 컨텐츠를 모두 입력 받는 캠페인이면 2세트 모두 필수 (각각 postUrl 포함)
     * <p> 아니면 첫 세트만 허용(두 번째 세트 전달 시 400)
     * <p> - 각 타입별로: 1차 존재 + 동일 타입이라면 이미 업로드한 2차 리뷰가 없다는 조건이 충족해야 함
     */
    @Transactional
    public ReviewUploadResponse uploadSecond(Long userId, Long campaignId, SecondReviewUploadRequest request) {
        Creator creator = creatorGetService.findByUserId(userId);
        Campaign campaign = campaignGetService.findByCampaignId(campaignId);
        CreatorCampaign participation =
                creatorCampaignGetService.getByCampaignAndCreatorId(campaign, creator.getId());

        ContentType typeA = campaign.getFirstContentPlatform();
        ContentType typeB = campaign.getSecondContentPlatform();
        CampaignReviewValidationUtil.validateTwoSetCombination(typeA, typeB);

        // A 세트(필수: 미디어/캡션/postUrl)
        CampaignReviewValidationUtil.requireSecondSetPresent(
                request.firstMediaUrls(), request.firstCaptionWithHashtags(), request.firstPostUrl());
        MediaValidationUtil.validateTotalMediaCount(request.firstMediaUrls());

        // B 세트(캠페인에 second가 있으면 필수, 없으면 금지)
        if (typeB != null) {
            CampaignReviewValidationUtil.requireSecondSetPresent(
                    request.secondMediaUrls(), request.secondCaptionWithHashtags(), request.secondPostUrl());
            MediaValidationUtil.validateTotalMediaCount(request.secondMediaUrls());
        } else {
            CampaignReviewValidationUtil.ensureSecondSetAbsentForSecondRound(
                    request.secondMediaUrls(), request.secondCaptionWithHashtags(), request.secondPostUrl());
        }

        // 미디어 합산 개수 제한
        CampaignReviewValidationUtil.validateCombinedMediaLimit(
                request.firstMediaUrls(),
                (typeB != null) ? request.secondMediaUrls() : null
        );

        // 선행/중복 검증 & 저장 A
        campaignReviewGetService.getFirstOrThrow(participation.getId(), typeA);

        CampaignReview secondA = campaignReviewMapper.toSecondReview(
                participation, typeA, request.firstCaptionWithHashtags(), request.firstPostUrl());
        CampaignReview savedA = campaignReviewSaveService.saveReview(secondA);
        campaignReviewSaveService.saveMedia(savedA, request.firstMediaUrls());

        // 2차 리뷰 업로드 시 SocialClip 생성 (성과 지표 0으로 초기화)
        socialClipSaveService.createForSecondReview(savedA);

        // B(옵션)
        if (typeB != null) {
            campaignReviewGetService.getFirstOrThrow(participation.getId(), typeB);
            CampaignReview secondB = campaignReviewMapper.toSecondReview(
                    participation, typeB, request.secondCaptionWithHashtags(), request.secondPostUrl());
            CampaignReview savedB = campaignReviewSaveService.saveReview(secondB);
            campaignReviewSaveService.saveMedia(savedB, request.secondMediaUrls());

            // 2차 리뷰 업로드 시 SocialClip 생성 (성과 지표 0으로 초기화)
            socialClipSaveService.createForSecondReview(savedB);
        }

        creatorCampaignUpdateService.refreshParticipationStatus(participation.getId());
        return campaignReviewMapper.toUploadResponse(savedA);
    }

    @Transactional
    public CampaignParticipatedResponse getMyReviewableCampaign(Long userId, Long campaignId, ReviewRound round) {
        Creator creator = creatorGetService.findByUserId(userId);

        // 베타버전에서는 여러 캠페인 상태에서 리뷰 업로드 가능
        CreatorCampaign creatorCampaign;
        if (betaFeatureConfig.isSimplifiedReviewFlow()) {
            // 베타버전: RECRUITING, RECRUITMENT_CLOSED, IN_REVIEW 상태 모두 허용
            creatorCampaign = creatorCampaignGetService.findReviewableInMultipleStatusesForBeta(
                    creator.getId(), campaignId);
        } else {
            // 정식버전: IN_REVIEW 상태만 허용
            creatorCampaign = creatorCampaignGetService.findReviewableInReviewByCampaign(
                    creator.getId(), campaignId);
        }

        // ACTIVE 상태에서만 업로드 가능
        if (creatorCampaign.getStatus() != ParticipationStatus.ACTIVE) {
            throw new CampaignReviewAbleNotFoundException();
        }

        // 조회 시점에 브랜드 노트가 있는 리뷰들의 noteViewed를 true로 업데이트
        markBrandNotesAsViewed(creatorCampaign);

        // round 파라미터가 있으면 해당 라운드만 필터링
        List<CampaignParticipatedResponse.ReviewContentStatus> reviewContents;
        if (round != null) {
            reviewContents = createRoundSpecificReviewContentStatuses(creatorCampaign, round);
        } else {
            reviewContents = createReviewContentStatuses(creatorCampaign);
        }

        return campaignMapper.toCampaignParticipationResponse(creatorCampaign, reviewContents);
    }

    @Transactional(readOnly = true)
    public List<CampaignParticipatedResponse> getMyReviewables(Long userId, ReviewRound round) {
        Creator creator = creatorGetService.findByUserId(userId);
        List<CreatorCampaign> eligibles = creatorCampaignGetService.findReviewable(creator.getId());

        return eligibles.stream()
                .filter(cc -> cc.getStatus() == ParticipationStatus.ACTIVE)  // ACTIVE만 필터링
                .map(creatorCampaign -> {
                    // round 파라미터가 있으면 해당 라운드만 필터링
                    List<CampaignParticipatedResponse.ReviewContentStatus> reviewContents;
                    if (round != null) {
                        reviewContents = createRoundSpecificReviewContentStatuses(creatorCampaign, round);
                    } else {
                        reviewContents = createReviewContentStatuses(creatorCampaign);
                    }

                    return campaignMapper.toCampaignParticipationResponse(creatorCampaign, reviewContents);
                })
                .filter(response -> !response.reviewContents().isEmpty())  // 빈 컨텐츠는 제외
                .toList();
    }

    /**
     * 실제 업로드 가능한 리뷰 라운드를 결정하는 메서드
     * ACTIVE 상태에서 기존 리뷰 존재 여부로 1차/2차 구분
     */
    private ReviewRound determineActualReviewRound(CreatorCampaign creatorCampaign) {
        // 1차 리뷰가 존재하는지 확인
        boolean hasFirstReview = campaignReviewGetService.existsFirst(creatorCampaign.getId());

        if (hasFirstReview) {
            // 1차 리뷰가 있으면 2차 업로드 차례
            return ReviewRound.SECOND;
        } else {
            // 1차 리뷰가 없으면 1차 업로드 차례
            return ReviewRound.FIRST;
        }
    }

    /**
     * 각 content type별 리뷰 상태를 생성하는 메서드
     */
    private List<CampaignParticipatedResponse.ReviewContentStatus> createReviewContentStatuses(CreatorCampaign creatorCampaign) {
        Campaign campaign = creatorCampaign.getCampaign();
        List<ContentType> campaignContentTypes = List.of(
                campaign.getFirstContentPlatform(),
                campaign.getSecondContentPlatform()
        ).stream().filter(ct -> ct != null).toList();

        // 기존 리뷰 현황 조회
        List<ContentType> existingFirstTypes = campaignReviewGetService
                .findContentTypesByRound(creatorCampaign.getId(), ReviewRound.FIRST);
        List<ContentType> existingSecondTypes = campaignReviewGetService
                .findContentTypesByRound(creatorCampaign.getId(), ReviewRound.SECOND);

        return campaignContentTypes.stream()
                .map(contentType -> {
                    boolean hasFirstReview = existingFirstTypes.contains(contentType);
                    boolean hasSecondReview = existingSecondTypes.contains(contentType);

                    ReviewRound nowRound = hasFirstReview ? ReviewRound.SECOND : ReviewRound.FIRST;

                    // 2차 리뷰 업로드 시에만 해당 content type의 1차 리뷰에서 브랜드 노트 정보 가져오기
                    String brandNote = null;
                    Instant revisionRequestedAt = null;

                    if (nowRound == ReviewRound.SECOND) {
                        // 해당 content type의 1차 리뷰에서 브랜드 노트 조회
                        Optional<CampaignReview> firstReviewForContentType = campaignReviewGetService
                                .findByContentType(creatorCampaign.getId(), ReviewRound.FIRST, contentType);

                        if (firstReviewForContentType.isPresent()) {
                            CampaignReview review = firstReviewForContentType.get();
                            brandNote = review.getBrandNote();
                            revisionRequestedAt = review.getRevisionRequestedAt();
                        }
                    }

                    // 기존 리뷰의 캡션과 미디어 URL 정보 가져오기
                    String captionWithHashtags = null;
                    List<String> mediaUrls = null;

                    // 현재 라운드에 해당하는 기존 리뷰가 있다면 정보 가져오기
                    ReviewRound reviewRoundToCheck = hasFirstReview ? ReviewRound.FIRST : null;
                    if (reviewRoundToCheck != null) {
                        Optional<CampaignReview> existingReview = campaignReviewGetService
                                .findByContentType(creatorCampaign.getId(), reviewRoundToCheck, contentType);

                        if (existingReview.isPresent()) {
                            CampaignReview review = existingReview.get();
                            captionWithHashtags = review.getCaptionWithHashtags();
                            mediaUrls = campaignReviewGetService.getOrderedMediaUrls(review);
                        }
                    }

                    return campaignMapper.toReviewContentStatus(contentType, nowRound, brandNote, revisionRequestedAt, captionWithHashtags, mediaUrls);
                })
                .filter(status -> {
                    // 이미 2차 리뷰까지 완료된 content type은 제외
                    boolean hasSecondReview = existingSecondTypes.contains(status.contentType());
                    return !hasSecondReview;
                })
                .toList();
    }

    /**
     * 특정 라운드에 해당하는 content type별 리뷰 상태를 생성하는 메서드
     */
    private List<CampaignParticipatedResponse.ReviewContentStatus> createRoundSpecificReviewContentStatuses(
            CreatorCampaign creatorCampaign, ReviewRound targetRound) {
        Campaign campaign = creatorCampaign.getCampaign();
        List<ContentType> campaignContentTypes = List.of(
                campaign.getFirstContentPlatform(),
                campaign.getSecondContentPlatform()
        ).stream().filter(ct -> ct != null).toList();

        // 기존 리뷰 현황 조회
        List<ContentType> existingFirstTypes = campaignReviewGetService
                .findContentTypesByRound(creatorCampaign.getId(), ReviewRound.FIRST);
        List<ContentType> existingSecondTypes = campaignReviewGetService
                .findContentTypesByRound(creatorCampaign.getId(), ReviewRound.SECOND);

        return campaignContentTypes.stream()
                .filter(contentType -> {
                    boolean hasFirstReview = existingFirstTypes.contains(contentType);
                    boolean hasSecondReview = existingSecondTypes.contains(contentType);

                    if (targetRound == ReviewRound.FIRST) {
                        // 1차 라운드 요청: 1차 리뷰가 없는 것들만
                        return !hasFirstReview;
                    } else {
                        // 2차 라운드 요청: 1차는 있고 2차는 없는 것들만
                        return hasFirstReview && !hasSecondReview;
                    }
                })
                .map(contentType -> {
                    // 2차 라운드 요청 시에만 브랜드 노트 정보 포함
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

                    // 기존 리뷰의 캡션과 미디어 URL 정보 가져오기
                    String captionWithHashtags = null;
                    List<String> mediaUrls = null;

                    if (targetRound == ReviewRound.SECOND) {
                        // 2차 리뷰 시에는 1차 리뷰 정보를 가져옴
                        Optional<CampaignReview> firstReviewForContentType = campaignReviewGetService
                                .findByContentType(creatorCampaign.getId(), ReviewRound.FIRST, contentType);

                        if (firstReviewForContentType.isPresent()) {
                            CampaignReview review = firstReviewForContentType.get();
                            captionWithHashtags = review.getCaptionWithHashtags();
                            mediaUrls = campaignReviewGetService.getOrderedMediaUrls(review);
                        }
                    }

                    return campaignMapper.toReviewContentStatus(contentType, targetRound, brandNote, revisionRequestedAt, captionWithHashtags, mediaUrls);
                })
                .toList();
    }

    /**
     * 완료된 2차 리뷰의 컨텐츠를 생성하는 메서드
     */
    private List<CompletedReviewResponse.CompletedReviewContent> createCompletedReviewContents(CreatorCampaign creatorCampaign) {
        // 실제 업로드된 2차 리뷰의 컨텐츠 타입들
        List<ContentType> existingSecondTypes = campaignReviewGetService
                .findContentTypesByRound(creatorCampaign.getId(), ReviewRound.SECOND);

        return existingSecondTypes.stream()
                .map(contentType -> {
                    // 2차 리뷰 정보 조회
                    Optional<CampaignReview> secondReview = campaignReviewGetService
                            .findByContentType(creatorCampaign.getId(), ReviewRound.SECOND, contentType);

                    if (secondReview.isPresent()) {
                        CampaignReview review = secondReview.get();
                        String captionWithHashtags = review.getCaptionWithHashtags();
                        List<String> mediaUrls = campaignReviewGetService.getOrderedMediaUrls(review);

                        return CompletedReviewResponse.CompletedReviewContent.builder()
                                .contentType(contentType)
                                .captionWithHashtags(captionWithHashtags)
                                .mediaUrls(mediaUrls)
                                .build();
                    }
                    return null;
                })
                .filter(content -> content != null)
                .toList();
    }

    /**
     * 완료된 1차 리뷰의 컨텐츠를 생성하는 메서드 (베타 기능)
     */
    private List<CompletedReviewResponse.CompletedReviewContent> createCompletedFirstReviewContents(CreatorCampaign creatorCampaign) {
        // 실제 업로드된 1차 리뷰의 컨텐츠 타입들
        List<ContentType> existingFirstTypes = campaignReviewGetService
                .findContentTypesByRound(creatorCampaign.getId(), ReviewRound.FIRST);

        return existingFirstTypes.stream()
                .map(contentType -> {
                    // 1차 리뷰 정보 조회
                    Optional<CampaignReview> firstReview = campaignReviewGetService
                            .findByContentType(creatorCampaign.getId(), ReviewRound.FIRST, contentType);

                    if (firstReview.isPresent()) {
                        CampaignReview review = firstReview.get();
                        String captionWithHashtags = review.getCaptionWithHashtags();
                        List<String> mediaUrls = campaignReviewGetService.getOrderedMediaUrls(review);

                        return CompletedReviewResponse.CompletedReviewContent.builder()
                                .contentType(contentType)
                                .captionWithHashtags(captionWithHashtags)
                                .mediaUrls(mediaUrls)
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 조회 시점에 브랜드 노트가 있는 모든 1차 리뷰의 noteViewed를 true로 업데이트
     */
    private void markBrandNotesAsViewed(CreatorCampaign creatorCampaign) {
        // 해당 캠페인의 모든 1차 리뷰 조회
        List<CampaignReview> firstReviews = campaignReviewGetService
                .getAllByCreatorCampaignAndRound(creatorCampaign, ReviewRound.FIRST);

        // 브랜드 노트가 있는 리뷰들의 noteViewed를 true로 업데이트
        firstReviews.stream()
                .filter(review -> review.getBrandNote() != null && !review.getBrandNote().isEmpty())
                .filter(review -> !review.isNoteViewed()) // 아직 확인하지 않은 것만
                .forEach(CampaignReview::markNoteAsViewed);
    }


    @Transactional(readOnly = true)
    public MediaPresignedUrlResponse createMediaPresignedUrl(Long userId, MediaPresignedUrlRequest request) {
        Creator creator = creatorGetService.findByUserId(userId);
        List<String> urls = campaignReviewUpdateService.createPresignedUrlForReview(creator.getId(), request);

        return campaignReviewMapper.toMediaPresignedUrlResponse(urls);
    }

    /**
     * 완료된 캠페인의 최종 리뷰 결과 조회
     * 베타 모드: 1차 리뷰 조회
     * 정식 모드: 2차 리뷰 조회 (2차가 없으면 1차 리뷰 조회)
     */
    @Transactional(readOnly = true)
    public CompletedReviewResponse getCompletedReviews(Long userId, Long campaignId) {
        Creator creator = creatorGetService.findByUserId(userId);
        Campaign campaign = campaignGetService.findByCampaignId(campaignId);
        CreatorCampaign creatorCampaign =
                creatorCampaignGetService.getByCampaignAndCreatorId(campaign, creator.getId());

        // COMPLETED 상태만 허용
        if (creatorCampaign.getStatus() != ParticipationStatus.COMPLETED) {
            throw new CampaignReviewAbleNotFoundException();
        }

        List<CompletedReviewResponse.CompletedReviewContent> reviewContents;

        if (betaFeatureConfig.isSimplifiedReviewFlow()) {
            // 베타 모드: 1차 리뷰 조회
            reviewContents = createCompletedFirstReviewContents(creatorCampaign);
        } else {
            reviewContents = createCompletedReviewContents(creatorCampaign);
        }

        return CompletedReviewResponse.builder()
                .campaignId(campaignId)
                .campaignName(campaign.getTitle())
                .reviewContents(reviewContents)
                .build();
    }
}
