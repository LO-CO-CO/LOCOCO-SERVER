package com.lokoko.domain.campaign.domain.repository;

import com.lokoko.domain.brand.api.dto.response.BrandDashboardCampaignListResponse;
import com.lokoko.domain.brand.api.dto.response.BrandDashboardCampaignResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignInfoListResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignInfoResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignListResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignResponse;
import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.campaign.api.dto.response.CampaignParticipatedResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageCampaignListResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageCampaignResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageUpcomingCampaignListResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageUpcomingCampaignResponse;
import com.lokoko.domain.campaign.domain.entity.QCampaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignChipStatus;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignLanguage;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductType;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductTypeFilter;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatusFilter;
import com.lokoko.domain.campaign.domain.entity.enums.LanguageFilter;
import com.lokoko.domain.campaignReview.domain.entity.QCampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewStatus;
import com.lokoko.domain.creatorCampaign.domain.entity.QCreatorCampaign;
import com.lokoko.domain.media.image.domain.entity.QCampaignImage;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import com.lokoko.domain.user.api.dto.request.ApprovedStatus;
import com.lokoko.domain.user.api.dto.response.AdminCampaignInfoResponse;
import com.lokoko.domain.user.api.dto.response.AdminCampaignListResponse;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.global.common.response.PageableResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

import static com.lokoko.domain.media.image.domain.entity.enums.ImageType.THUMBNAIL;

@Repository
@RequiredArgsConstructor
public class CampaignRepositoryImpl implements CampaignRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QCampaign campaign = QCampaign.campaign;
    private final QCampaignImage campaignImage = QCampaignImage.campaignImage;
    private final QCampaignReview campaignReview = QCampaignReview.campaignReview;
    private final QCreatorCampaign creatorCampaign = QCreatorCampaign.creatorCampaign;

    private final UserRepository userRepository;

    /**
     * 현재시간이 applyStartDate(캠페인 시작시간) 와 reviewSubmissionDeadline(캠페인 종료시간) 사이에 있어야한다
     */
    @Override
    public BrandMyCampaignInfoListResponse findSimpleCampaignInfoByBrandId(Long brandId) {
        Instant now = Instant.now();

        List<BrandMyCampaignInfoResponse> simpleResponses = queryFactory
                .select(Projections.constructor(BrandMyCampaignInfoResponse.class,
                        campaign.id,
                        campaign.title,
                        campaign.applyStartDate,
                        campaign.reviewSubmissionDeadline))
                .from(campaign)
                .where(campaign.brand.id.eq(brandId)
                        .and(campaign.applyStartDate.loe(now)))
                .orderBy(campaign.applyStartDate.asc(), campaign.title.asc())
                .fetch();

        return new BrandMyCampaignInfoListResponse(simpleResponses);
    }

    @Override
    public BrandMyCampaignListResponse findBrandMyCampaigns(Long brandId, CampaignStatusFilter filterStatus, Pageable pageable) {
        Instant now = Instant.now();

        StringExpression statusCase = new CaseBuilder()
                .when(campaign.campaignStatus.eq(CampaignStatus.DRAFT))
                .then(CampaignStatus.DRAFT.name())
                .when(campaign.campaignStatus.eq(CampaignStatus.WAITING_APPROVAL))
                .then(CampaignStatus.WAITING_APPROVAL.name())
                .when(Expressions.asDateTime(now).before(campaign.applyStartDate))
                .then(CampaignStatus.OPEN_RESERVED.name())
                .when(Expressions.asDateTime(now).before(campaign.applyDeadline))
                .then(CampaignStatus.RECRUITING.name())
                .when(Expressions.asDateTime(now).before(campaign.creatorAnnouncementDate))
                .then(CampaignStatus.RECRUITMENT_CLOSED.name())
                .when(Expressions.asDateTime(now).before(campaign.reviewSubmissionDeadline))
                .then(CampaignStatus.IN_REVIEW.name())
                .otherwise(CampaignStatus.COMPLETED.name());

        BooleanExpression condition = campaign.brand.id.eq(brandId);

        if (filterStatus != null && !"ALL".equals(filterStatus.name())) {
            if ("ACTIVE".equals(filterStatus.name())) {
                condition = condition.and(
                        statusCase.eq(CampaignStatus.RECRUITING.name())
                                .or(statusCase.eq(CampaignStatus.RECRUITMENT_CLOSED.name()))
                                .or(statusCase.eq(CampaignStatus.IN_REVIEW.name()))
                );
            } else {
                condition = condition.and(statusCase.eq(filterStatus.name()));
            }
        }

        List<BrandMyCampaignResponse> campaigns = queryFactory
                .select(
                        campaign.id,
                        campaignImage.mediaFile.fileUrl,
                        campaign.title,
                        campaign.applyDeadline,
                        campaign.applicantNumber,
                        campaign.recruitmentNumber,
                        statusCase
                )
                .from(campaign)
                .leftJoin(campaignImage).on(
                        campaignImage.campaign.eq(campaign)
                                .and(campaignImage.displayOrder.eq(0))
                                .and(campaignImage.imageType.eq(THUMBNAIL))
                )
                .where(condition)
                .orderBy(campaign.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(tuple -> new BrandMyCampaignResponse(
                        tuple.get(campaign.id),
                        tuple.get(campaignImage.mediaFile.fileUrl),
                        tuple.get(campaign.title),
                        tuple.get(campaign.applyDeadline),
                        tuple.get(campaign.applicantNumber),
                        tuple.get(campaign.recruitmentNumber),
                        tuple.get(statusCase)
                ))
                .toList();

        Long totalCount = queryFactory
                .select(campaign.count())
                .from(campaign)
                .where(condition)
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;
        boolean isLast = (pageable.getOffset() + pageable.getPageSize()) >= total;

        PageableResponse pageInfo = PageableResponse.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                campaigns.size(),
                isLast,
                total
        );

        return new BrandMyCampaignListResponse(campaigns, pageInfo);
    }

    @Override
    public MainPageUpcomingCampaignListResponse findUpcomingCampaignsInMainPage(LanguageFilter lang,
                                                                                CampaignProductTypeFilter category) {

        BooleanExpression langCondition = buildLanguageCondition(lang);
        BooleanExpression categoryCondition = buildCategoryCondition(category);

        BooleanExpression languageAndCategoryCondition = combineConditions(
                langCondition,
                categoryCondition
        );

        // OPEN_RESERVED 상태인 캠페인만 조회하면 된다.
        List<MainPageUpcomingCampaignResponse> upcomingCampaigns = queryFactory
                .select(Projections.constructor(MainPageUpcomingCampaignResponse.class,
                        campaign.id,
                        campaign.campaignType,
                        campaign.language,
                        campaign.brand.brandName,
                        campaignImage.mediaFile.fileUrl,
                        campaign.title,
                        campaign.applicantNumber,
                        campaign.recruitmentNumber,
                        campaign.applyStartDate,
                        Expressions.constant(CampaignChipStatus.DISABLED.getDisplayName())
                ))
                .from(campaign)
                .innerJoin(campaignImage).on(campaignImage.campaign.eq(campaign)
                        .and(campaignImage.displayOrder.eq(0))
                        .and(campaignImage.imageType.eq((THUMBNAIL))))
                .where(campaign.campaignStatus.eq(CampaignStatus.OPEN_RESERVED).and(languageAndCategoryCondition))
                .orderBy(campaign.applyStartDate.asc())
                .limit(6)
                .fetch();

        return new MainPageUpcomingCampaignListResponse(upcomingCampaigns);

    }

    @Override
    public MainPageCampaignListResponse findCampaignsInMainPage(Long userId, LanguageFilter lang,
                                                                CampaignProductTypeFilter category, Pageable pageable) {

        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        Instant now = Instant.now();

        BooleanExpression langCondition = buildLanguageCondition(lang);
        BooleanExpression categoryCondition = buildCategoryCondition(category);

        // DRAFT, WAITING_APPROVAL만 제외하고, 실시간으로 아직 시작되지 않은 캠페인도 제외
        BooleanExpression statusCondition = campaign.campaignStatus.notIn(
                CampaignStatus.DRAFT,
                CampaignStatus.WAITING_APPROVAL
        ).and(campaign.applyStartDate.loe(now)); // 모집 시작일이 현재 시간보다 이전이어야 함

        BooleanExpression condition = combineConditions(
                langCondition,
                categoryCondition,
                statusCondition
        );

        StringExpression chipStatusExpression = createChipStatusExpression();

        List<MainPageCampaignResponse> campaignList = queryFactory
                .select(Projections.constructor(MainPageCampaignResponse.class,
                        campaign.id,
                        campaign.campaignType,
                        campaign.language,
                        campaign.brand.brandName,
                        campaignImage.mediaFile.fileUrl,
                        campaign.title,
                        campaign.applicantNumber,
                        campaign.recruitmentNumber,
                        campaign.reviewSubmissionDeadline,
                        chipStatusExpression
                ))
                .from(campaign)
                .innerJoin(campaignImage).on(campaignImage.campaign.eq(campaign)
                        .and(campaignImage.displayOrder.eq(0))
                        .and(campaignImage.imageType.eq((THUMBNAIL))))
                .where(condition)
                .orderBy(campaign.applyDeadline.asc()) // 마감기한 얼마 남지 않은 순
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(campaign.count())
                .from(campaign)
                .innerJoin(campaignImage).on(campaignImage.campaign.eq(campaign)
                        .and(campaignImage.displayOrder.eq(0))
                        .and(campaignImage.imageType.eq((THUMBNAIL))))
                .where(condition)
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;
        boolean isLast = (pageable.getOffset() + pageable.getPageSize()) >= total;

        PageableResponse pageInfo = PageableResponse.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                campaignList.size(),
                isLast,
                total);

        return new MainPageCampaignListResponse(campaignList, pageInfo);
    }

    @Override
    public List<CampaignParticipatedResponse> findInReviewCampaignTitlesByBrand(Brand brand) {
        QCampaign campaign = QCampaign.campaign;

        return queryFactory
                .select(Projections.constructor(
                        CampaignParticipatedResponse.class,
                        campaign.id,
                        campaign.title
                ))
                .from(campaign)
                .where(
                        campaign.brand.eq(brand),
                        campaign.isPublished.isTrue(),
                        campaign.campaignStatus.eq(CampaignStatus.IN_REVIEW)
                )
                .orderBy(campaign.title.asc())
                .fetch();
    }

    private BooleanExpression buildLanguageCondition(LanguageFilter lang) {
        if (lang == null || lang == LanguageFilter.ALL) {
            return null; // 모든 언어 포함
        }
        CampaignLanguage campaignLanguage = CampaignLanguage.valueOf(lang.name());
        return campaign.language.eq(campaignLanguage);
    }

    private BooleanExpression buildCategoryCondition(CampaignProductTypeFilter category) {
        if (category == null || category == CampaignProductTypeFilter.ALL) {
            return null; // 모든 카테고리 포함
        }
        CampaignProductType campaignProductType = CampaignProductType.valueOf(category.name());
        return campaign.campaignProductType.eq(campaignProductType);
    }

    private BooleanExpression combineConditions(BooleanExpression... expressions) {
        BooleanExpression result = null;
        for (BooleanExpression expression : expressions) {
            if (expression != null) {
                result = (result == null) ? expression : result.and(expression);
            }
        }
        return result;
    }

    private StringExpression createChipStatusExpression() {
        Instant now = Instant.now();

        return new CaseBuilder()
                .when(campaign.applyStartDate.loe(now)
                        .and(campaign.applyDeadline.gt(now)))
                .then(CampaignChipStatus.DEFAULT.getDisplayName())
                .when(campaign.applyDeadline.loe(now))
                .then(CampaignChipStatus.DISABLED.getDisplayName())
                .otherwise(Expressions.nullExpression(String.class));
    }

    @Override
    public BrandDashboardCampaignListResponse findBrandDashboardCampaigns(Long brandId, Pageable pageable) {
        Instant now = Instant.now();

        List<BrandDashboardCampaignResponse> campaigns = queryFactory
                .select(Projections.constructor(BrandDashboardCampaignResponse.class,
                        campaign.id,
                        campaignImage.mediaFile.fileUrl,
                        campaign.title,
                        campaign.applyStartDate,
                        campaign.reviewSubmissionDeadline,
                        // DB에 저장된 캠페인 상태
                        campaign.campaignStatus,
                        campaign.approvedNumber,
                        // 인스타 포스트 개수
                        JPAExpressions.select(campaignReview.count().coalesce(0L))
                                .from(campaignReview)
                                .join(campaignReview.creatorCampaign, creatorCampaign)
                                .where(creatorCampaign.campaign.eq(campaign)
                                        .and(campaignReview.reviewRound.eq(ReviewRound.SECOND))
                                        .and(campaignReview.status.eq(ReviewStatus.RESUBMITTED))
                                        .and(campaignReview.contentType.eq(ContentType.INSTA_POST))),
                        // 인스타 릴스 개수
                        JPAExpressions.select(campaignReview.count().coalesce(0L))
                                .from(campaignReview)
                                .join(campaignReview.creatorCampaign, creatorCampaign)
                                .where(creatorCampaign.campaign.eq(campaign)
                                        .and(campaignReview.reviewRound.eq(ReviewRound.SECOND))
                                        .and(campaignReview.status.eq(ReviewStatus.RESUBMITTED))
                                        .and(campaignReview.contentType.eq(ContentType.INSTA_REELS))),
                        // 틱톡 개수
                        JPAExpressions.select(campaignReview.count().coalesce(0L))
                                .from(campaignReview)
                                .join(campaignReview.creatorCampaign, creatorCampaign)
                                .where(creatorCampaign.campaign.eq(campaign)
                                        .and(campaignReview.reviewRound.eq(ReviewRound.SECOND))
                                        .and(campaignReview.status.eq(ReviewStatus.RESUBMITTED))
                                        .and(campaignReview.contentType.eq(ContentType.TIKTOK_VIDEO)))
                ))
                .from(campaign)
                .leftJoin(campaignImage).on(
                        campaignImage.campaign.eq(campaign)
                                .and(campaignImage.displayOrder.eq(0))
                                .and(campaignImage.imageType.eq(THUMBNAIL))
                )
                .where(campaign.brand.id.eq(brandId)
                        .and(campaign.applyStartDate.loe(now)))
                .orderBy(campaign.reviewSubmissionDeadline.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(campaign.count())
                .from(campaign)
                .where(campaign.brand.id.eq(brandId)
                        .and(campaign.applyStartDate.loe(now))
                )
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;
        boolean isLast = (pageable.getOffset() + pageable.getPageSize()) >= total;

        PageableResponse pageInfo = PageableResponse.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                campaigns.size(),
                isLast,
                total
        );

        return new BrandDashboardCampaignListResponse(campaigns, pageInfo);
    }

    @Override
    public AdminCampaignListResponse findAllCampaignsByAdmin(ApprovedStatus status, Pageable pageable) {

        StringExpression approvedStatus = new CaseBuilder()
                .when(campaign.campaignStatus.eq(CampaignStatus.WAITING_APPROVAL))
                .then("PENDING")
                .when(campaign.campaignStatus.in(CampaignStatus.getApprovedStatuses()))
                .then("APPROVED")
                .otherwise("PENDING");

        BooleanExpression statusCondition = createStatusCondition(status);

        List<AdminCampaignInfoResponse> campaignList = queryFactory
                .select(Projections.constructor(AdminCampaignInfoResponse.class,
                        campaign.id,
                        campaign.brand.brandName,
                        campaign.title,
                        Projections.constructor(AdminCampaignInfoResponse.RecruitmentStatus.class,
                                campaign.recruitmentNumber,
                                campaign.applicantNumber),
                        campaign.applyStartDate,
                        campaign.applyDeadline,
                        approvedStatus
                ))
                .from(campaign)
                .where(statusCondition, campaign.campaignStatus.ne(CampaignStatus.DRAFT))
                .orderBy(campaign.publishedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long totalCount = queryFactory
                .select(campaign.count())
                .from(campaign)
                .where(statusCondition)
                .fetchOne();


        long total = totalCount != null ? totalCount : 0L;
        boolean isLast = (pageable.getOffset() + pageable.getPageSize()) >= total;

        PageableResponse pageInfo = PageableResponse.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                campaignList.size(),
                isLast,
                total
        );

        return new AdminCampaignListResponse(campaignList, pageInfo);
    }

    private BooleanExpression createStatusCondition(ApprovedStatus status) {
        if (status == null) {
            return null; // 필터링 없음
        }

        return switch (status) {
            case PENDING -> campaign.campaignStatus.eq(CampaignStatus.WAITING_APPROVAL);
            case APPROVED ->
                    campaign.campaignStatus.in(CampaignStatus.getApprovedStatuses());
        };
    }
}
