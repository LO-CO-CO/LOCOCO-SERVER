package com.lokoko.domain.campaign.domain.repository;

import com.lokoko.domain.brand.api.dto.response.BrandDashboardCampaignListResponse;
import com.lokoko.domain.brand.api.dto.response.BrandDashboardCampaignResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignInfoListResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignInfoResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignListResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignResponse;
import com.lokoko.domain.brand.api.dto.response.CampaignDashboard;
import com.lokoko.domain.campaign.api.dto.response.MainPageCampaignListResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageCampaignResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageUpcomingCampaignListResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageUpcomingCampaignResponse;
import com.lokoko.domain.campaign.domain.entity.QCampaign;
import com.lokoko.domain.campaign.domain.entity.enums.*;
import com.lokoko.domain.campaignReview.domain.entity.QCampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewStatus;
import com.lokoko.domain.creatorCampaign.domain.entity.QCreatorCampaign;
import com.lokoko.domain.image.domain.entity.QCampaignImage;
import com.lokoko.domain.image.domain.entity.enums.ImageType;
import com.lokoko.domain.socialclip.domain.entity.enums.ContentType;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.global.common.response.PageableResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.lokoko.domain.campaignReview.domain.entity.QCampaignReview.campaignReview;
import static com.lokoko.domain.creatorCampaign.domain.entity.QCreatorCampaign.creatorCampaign;
import static com.lokoko.domain.image.domain.entity.enums.ImageType.THUMBNAIL;

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
                        .and(campaign.applyStartDate.loe(now))
                        .and(campaign.reviewSubmissionDeadline.goe(now)))
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

        PageableResponse pageInfo = new PageableResponse(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                campaigns.size(),
                isLast
        );

        return new BrandMyCampaignListResponse(campaigns, pageInfo);
    }

    @Override
    public MainPageUpcomingCampaignListResponse findUpcomingCampaignsInMainPage(LanguageFilter lang, CampaignProductTypeFilter category) {

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
    public MainPageCampaignListResponse findCampaignsInMainPage(Long userId, LanguageFilter lang, CampaignProductTypeFilter category, Pageable pageable) {

        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        BooleanExpression langCondition = buildLanguageCondition(lang);
        BooleanExpression categoryCondition = buildCategoryCondition(category);
        BooleanExpression visibilityCondition = buildVisibilityCondition(user);

        // DRAFT, WAITING_APPROVAL, OPEN_RESERVED 상태 제외
        BooleanExpression statusCondition = campaign.campaignStatus.notIn(
                CampaignStatus.DRAFT,
                CampaignStatus.WAITING_APPROVAL,
                CampaignStatus.OPEN_RESERVED
        );

        BooleanExpression condition = combineConditions(
                langCondition,
                categoryCondition,
                visibilityCondition,
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

        PageableResponse pageInfo = new PageableResponse(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                campaignList.size(),
                isLast);

        return new MainPageCampaignListResponse(campaignList, pageInfo);
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

    private BooleanExpression buildVisibilityCondition(User user) {
        // 브랜드 사용자는 모든 캠페인을 볼 수 있다
        if (user != null && user.getRole() == Role.BRAND) {
            return null;
        }

        // 크리에이터 또는 비로그인 사용자는 캠페인 종료 후 30일까지만 볼 수 있으므로
        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);

        // reviewSubmissionDeadline이 null이거나, 종료 후 30일이 지나지 않은 캠페인
        return campaign.reviewSubmissionDeadline.isNull()
                .or(campaign.reviewSubmissionDeadline.after(thirtyDaysAgo));
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
        DateTimeExpression<Instant> now = DateTimeExpression.currentTimestamp(Instant.class);

        return new CaseBuilder()
                .when(now.after(campaign.applyStartDate)
                        .and(now.before(campaign.applyDeadline)))
                .then(CampaignChipStatus.DEFAULT.getDisplayName())
                .when(now.after(campaign.applyDeadline))
                .then(CampaignChipStatus.DISABLED.getDisplayName())
                .otherwise(Expressions.nullExpression(String.class));
    }

    @Override
    public List<CampaignDashboard> findBrandDashboardCampaigns(Long brandId, Pageable pageable) {

        return queryFactory
                .select(Projections.constructor(CampaignDashboard.class,
                        campaign.id,
                        campaign.title,
                        campaignImage.mediaFile.fileUrl,
                        campaign.applyStartDate,
                        campaign.applyDeadline,
                        campaign.creatorAnnouncementDate,
                        campaign.reviewSubmissionDeadline,
                        campaign.campaignStatus,
                        campaign.approvedNumber,
                        JPAExpressions.select(campaignReview.count().coalesce(0L))
                                .from(campaignReview)
                                .join(campaignReview.creatorCampaign, creatorCampaign)
                                .where(creatorCampaign.campaign.eq(campaign)
                                        .and(campaignReview.reviewRound.eq(ReviewRound.SECOND))
                                        .and(campaignReview.status.eq(ReviewStatus.RESUBMITTED))
                                        .and(campaignReview.contentType.eq(ContentType.INSTA_POST))),
                        JPAExpressions.select(campaignReview.count().coalesce(0L))
                                .from(campaignReview)
                                .join(campaignReview.creatorCampaign, creatorCampaign)
                                .where(creatorCampaign.campaign.eq(campaign)
                                        .and(campaignReview.reviewRound.eq(ReviewRound.SECOND))
                                        .and(campaignReview.status.eq(ReviewStatus.RESUBMITTED))
                                        .and(campaignReview.contentType.eq(ContentType.INSTA_REELS))),
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
                                .and(campaignImage.imageType.eq(ImageType.THUMBNAIL))
                )
                .where(campaign.brand.id.eq(brandId))
                .orderBy(campaign.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public Long countBrandDashboardCampaigns(Long brandId) {
        Long count = queryFactory
                .select(campaign.count())
                .from(campaign)
                .where(campaign.brand.id.eq(brandId))
                .fetchOne();
        return count != null ? count : 0L;
    }
}
