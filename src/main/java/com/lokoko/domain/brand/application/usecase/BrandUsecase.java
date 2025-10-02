package com.lokoko.domain.brand.application.usecase;

import com.lokoko.domain.brand.api.dto.request.BrandInfoUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandMyPageUpdateRequest;
import com.lokoko.domain.brand.api.dto.request.BrandProfileImageRequest;
import com.lokoko.domain.brand.api.dto.response.BrandIssuedCampaignResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyPageResponse;
import com.lokoko.domain.brand.api.dto.response.BrandProfileAndStatisticsResponse;
import com.lokoko.domain.brand.api.dto.response.BrandProfileImageResponse;
import com.lokoko.domain.brand.api.dto.response.CreatorPerformanceResponse;
import com.lokoko.domain.brand.application.service.BrandGetService;
import com.lokoko.domain.brand.application.service.BrandUpdateService;
import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.campaign.application.mapper.CampaignMapper;
import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.exception.NotCampaignOwnershipException;
import com.lokoko.domain.campaignReview.api.dto.response.CampaignReviewDetailListResponse;
import com.lokoko.domain.campaignReview.application.mapper.CampaignReviewMapper;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewGetService;
import com.lokoko.domain.campaignReview.application.service.CampaignReviewStatusManager;
import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ContentStatus;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewStatus;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creator.api.dto.response.CreatorInfo;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.media.socialclip.application.service.SocialClipGetService;
import com.lokoko.domain.media.socialclip.domain.SocialClip;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.global.common.response.PageableResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandUsecase {

    private final BrandGetService brandGetService;
    private final CampaignGetService campaignGetService;
    private final CreatorCampaignGetService creatorCampaignGetService;
    private final CampaignReviewGetService campaignReviewGetService;
    private final SocialClipGetService socialClipGetService;

    private final BrandUpdateService brandUpdateService;

    private final CampaignReviewStatusManager campaignReviewStatusManager;

    private final CampaignMapper campaignMapper;
    private final CampaignReviewMapper campaignReviewMapper;

    @Transactional
    public BrandProfileImageResponse createBrandProfilePresignedUrl(Long brandId, BrandProfileImageRequest request) {
        Brand brand = brandGetService.getBrandById(brandId);

        return brandUpdateService.createBrandProfilePresignedUrl(brand, request);
    }

    @Transactional(readOnly = true)
    public BrandMyPageResponse getBrandMyPage(Long brandId) {
        Brand brand = brandGetService.getBrandWithUserById(brandId);

        return BrandMyPageResponse.from(brand, brand.getUser());
    }

    @Transactional(readOnly = true)
    public BrandProfileAndStatisticsResponse getBrandProfileAndStatistics(Long brandId) {
        Brand brand = brandGetService.getBrandById(brandId);

        Instant now = Instant.now();
        int ongoingCampaigns = campaignGetService.countOngoingCampaigns(brandId, now);
        int completedCampaigns = campaignGetService.countCompletedCampaigns(brandId, now);

        return BrandProfileAndStatisticsResponse.of(brand, ongoingCampaigns, completedCampaigns);
    }

    @Transactional(readOnly = true)
    public List<BrandIssuedCampaignResponse> getMyIssuedCampaignsInReview(Long brandId) {
        Brand brand = brandGetService.getBrandById(brandId);
        List<Campaign> campaigns = campaignGetService.getBrandIssuedCampaignsInReview(brand);

        return campaigns.stream()
                .map(campaignMapper::toBrandIssuedCampaignResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CampaignReviewDetailListResponse getCreatorCampaignReview(Long brandId, Long campaignReviewId) {

        // 캠페인 리뷰 ID로 캠페인 리뷰 조회
        CampaignReview review = campaignReviewGetService.findById(campaignReviewId);

        // 연관 엔티티들
        CreatorCampaign creatorCampaign = review.getCreatorCampaign();
        Campaign campaign = creatorCampaign.getCampaign();
        Creator creator = creatorCampaign.getCreator();

        // 브랜드 권한 검증 (해당 브랜드가 발행한 캠페인인지 아닌지)
        if (!campaign.getBrand().getId().equals(brandId)) {
            throw new NotCampaignOwnershipException();
        }

        // 현재 라운드 결정 (일단 미사용)
        //ReviewRound round = campaignReviewStatusManager.determineReviewRound(
        //        campaign.getCampaignStatus(),
        //        creatorCampaign.getStatus()
        //);

        // 현재 DB에 저장된 round를 가져와서 검증
        ReviewRound round = review.getReviewRound();

        // 업로드 된 미디어 URL 조회
        List<String> mediaUrls = campaignReviewGetService.getOrderedMediaUrls(review);

        // 만약 2차 리뷰이고, postUrl이 존재한다면 같이 내려주기
        String postUrl = null;
        if (round == ReviewRound.SECOND && review.getPostUrl() != null) {
            postUrl = review.getPostUrl();
        }

        // 크리에이터 정보
        CreatorInfo creatorInfo = CreatorInfo.builder()
                .creatorId(creator.getId())
                .creatorFullName(creator.getUser().getName())
                .creatorNickname(creator.getCreatorName())
                .profileImageUrl(creator.getUser().getProfileImageUrl())
                .build();

        // 검토 요청 시간 (브랜드가 수정 요청한 시간)
        Instant reviewRequestedAt = review.getRevisionRequestedAt();

        return campaignReviewMapper.toDetailListResponse(
                campaign,
                review,
                round,
                mediaUrls,
                postUrl,
                creatorInfo,
                reviewRequestedAt
        );
    }

    @Transactional
    public void updateBrandInfo(Long brandId, BrandInfoUpdateRequest request) {
        Brand brand = brandGetService.getBrandById(brandId);
        brandUpdateService.updateBrandInfo(brand, request);
    }

    @Transactional
    public void updateBrandMyPage(Long brandId, BrandMyPageUpdateRequest request) {
        Brand brand = brandGetService.getBrandById(brandId);
        brandUpdateService.updateBrandMyPage(brand, request);
    }

    /**
     * 브랜드 컨텐츠 확인 API - 캠페인별 크리에이터 성과 조회
     */
    @Transactional(readOnly = true)
    public CreatorPerformanceResponse getCreatorPerformances(Long brandId, Long campaignId, int page, int size) {
        Brand brand = brandGetService.getBrandById(brandId);
        Campaign campaign = campaignGetService.findByCampaignId(campaignId);

        // 브랜드 권한 검증
        if (!campaign.getBrand().getId().equals(brandId)) {
            throw new NotCampaignOwnershipException();
        }

        // 해당 캠페인에 참여한 승인된 CreatorCampaign만 조회 (REJECTED 제외)
        List<CreatorCampaign> creatorCampaigns = creatorCampaignGetService.findAllByCampaign(campaign).stream()
                .filter(cc -> cc.getStatus() != ParticipationStatus.REJECTED)
                .toList();

        // 크리에이터별로 그룹화하여 리뷰 정보 구성
        List<CreatorPerformanceResponse.CreatorReviewPerformance> allCreatorPerformances = creatorCampaigns.stream()
                .collect(Collectors.groupingBy(CreatorCampaign::getCreator))
                .entrySet().stream()
                .map(entry -> {
                    Creator creator = entry.getKey();
                    List<CreatorCampaign> ccList = entry.getValue();

                    // 해당 크리에이터의 모든 리뷰 조회
                    List<CreatorPerformanceResponse.ReviewPerformance> reviews = buildReviewPerformances(campaign, ccList);

                    return CreatorPerformanceResponse.CreatorReviewPerformance.builder()
                            .creator(CreatorInfo.builder()
                                    .creatorId(creator.getId())
                                    .creatorFullName(creator.getUser().getName())
                                    .creatorNickname(creator.getCreatorName())
                                    .profileImageUrl(creator.getUser().getProfileImageUrl())
                                    .build())
                            .reviews(reviews)
                            .build();
                })
                .collect(Collectors.toList());

        // 페이징 처리
        long totalElements = allCreatorPerformances.size();
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, allCreatorPerformances.size());

        List<CreatorPerformanceResponse.CreatorReviewPerformance> pagedCreatorPerformances =
                allCreatorPerformances.subList(startIndex, endIndex);

        PageableResponse pageableResponse = PageableResponse.of(
                page,
                size,
                pagedCreatorPerformances.size(),
                endIndex >= allCreatorPerformances.size(),
                totalElements
        );

        return CreatorPerformanceResponse.builder()
                .campaignId(campaign.getId())
                .campaignTitle(campaign.getTitle())
                .firstContentPlatform(campaign.getFirstContentPlatform())
                .secondContentPlatform(campaign.getSecondContentPlatform())
                .creators(pagedCreatorPerformances)
                .pageableResponse(pageableResponse)
                .build();
    }

    /**
     * 크리에이터의 리뷰 성과 정보 구성
     */
    private List<CreatorPerformanceResponse.ReviewPerformance> buildReviewPerformances(
            Campaign campaign, List<CreatorCampaign> creatorCampaigns) {

        // 캠페인의 콘텐츠 타입들
        List<ContentType> contentTypes = new ArrayList<>();
        contentTypes.add(campaign.getFirstContentPlatform());
        if (campaign.getSecondContentPlatform() != null) {
            contentTypes.add(campaign.getSecondContentPlatform());
        }

        List<CreatorPerformanceResponse.ReviewPerformance> performances = new ArrayList<>();

        // 각 CreatorCampaign에 대해 처리
        for (CreatorCampaign cc : creatorCampaigns) {
            // 해당 CreatorCampaign의 모든 리뷰 조회
            List<CampaignReview> reviews = campaignReviewGetService.findAllByCreatorCampaignId(cc.getId());

            // contentType별로 리뷰를 맵으로 구성 (1차/2차 구분)
            Map<ContentType, CampaignReview> firstReviews = reviews.stream()
                    .filter(r -> r.getReviewRound() == ReviewRound.FIRST)
                    .collect(Collectors.toMap(CampaignReview::getContentType, r -> r, (a, b) -> a));

            Map<ContentType, CampaignReview> secondReviews = reviews.stream()
                    .filter(r -> r.getReviewRound() == ReviewRound.SECOND)
                    .collect(Collectors.toMap(CampaignReview::getContentType, r -> r, (a, b) -> a));

            // 각 contentType에 대해 리뷰 성과 정보 생성
            for (ContentType contentType : contentTypes) {
                CampaignReview secondReview = secondReviews.get(contentType);
                CampaignReview firstReview = firstReviews.get(contentType);

                if (secondReview != null) {
                    // 2차 리뷰가 있는 경우
                    performances.add(buildReviewPerformance(secondReview));
                } else if (firstReview != null) {
                    // 1차 리뷰만 있는 경우
                    performances.add(buildReviewPerformance(firstReview));
                } else {
                    // 리뷰가 없는 경우
                    ContentStatus contentStatus;

                    // 배송지 입력 여부에 따라 상태 결정
                    if (cc.getAddressConfirmed() != null && cc.getAddressConfirmed()) {
                        // 배송지는 입력했지만 1차 리뷰 미업로드 = 진행중
                        contentStatus = ContentStatus.IN_PROGRESS;
                    } else {
                        // 배송지 미입력 = 미제출
                        contentStatus = ContentStatus.NOT_SUBMITTED;
                    }

                    performances.add(CreatorPerformanceResponse.ReviewPerformance.builder()
                            .reviewRound(ReviewRound.FIRST)
                            .reviewStatus(contentStatus)
                            .contents(CreatorPerformanceResponse.ContentMetrics.builder()
                                    .contentType(contentType)
                                    .build())
                            .build());
                }
            }
        }

        return performances;
    }

    /**
     * 개별 리뷰의 성과 정보 생성
     */
    private CreatorPerformanceResponse.ReviewPerformance buildReviewPerformance(CampaignReview review) {
        ContentStatus contentStatus = getReviewContentStatus(review);
        String postUrl = null;
        Long viewCount = null;
        Long likeCount = null;
        Long commentCount = null;
        Long shareCount = null;
        Instant uploadedAt = null;

        // 2차 리뷰(최종 업로드)인 경우에만 postUrl과 성과 지표 포함
        if (review.getReviewRound() == ReviewRound.SECOND && review.getStatus() == ReviewStatus.RESUBMITTED) {
            postUrl = review.getPostUrl();

            // SocialClip에서 성과 지표 조회
            Optional<SocialClip> socialClip = socialClipGetService.findByCampaignReview(review);
            if (socialClip.isPresent()) {
                SocialClip clip = socialClip.get();
                viewCount = clip.getPlays();
                likeCount = clip.getLikes();
                commentCount = clip.getComments();
                shareCount = clip.getShares();
                uploadedAt = clip.getUploadedAt();
            }
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

    /**
     * 리뷰 상태를 ContentStatus enum으로 변환
     * PENDING_REVISION: 브랜드 리뷰 대기중 또는 수정 요청 후 크리에이터 노트 미확인
     * REVISING: 브랜드가 수정 요청 + 크리에이터가 노트 확인
     */
    private ContentStatus getReviewContentStatus(CampaignReview review) {
        ReviewStatus status = review.getStatus();

        return switch (status) {
            case SUBMITTED -> ContentStatus.PENDING_REVISION;
            case REVISION_REQUESTED -> {
                if (review.isNoteViewed()) {
                    yield ContentStatus.REVISING;
                } else {
                    yield ContentStatus.PENDING_REVISION;
                }
            }
            case RESUBMITTED -> ContentStatus.FINAL_UPLOADED;
        };
    }
}
