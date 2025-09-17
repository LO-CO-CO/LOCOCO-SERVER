package com.lokoko.domain.campaign.domain.repository;

import com.lokoko.domain.campaign.api.dto.response.MainPageCampaignListResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageCampaignResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageUpcomingCampaignListResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageUpcomingCampaignResponse;
import com.lokoko.domain.campaign.domain.entity.QCampaign;
import com.lokoko.domain.campaign.domain.entity.enums.*;
import com.lokoko.domain.image.domain.entity.QCampaignImage;
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
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.lokoko.domain.image.domain.entity.enums.ImageType.TOP;

@Repository
@RequiredArgsConstructor
public class CampaignRepositoryImpl implements CampaignRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QCampaign campaign = QCampaign.campaign;
    private final QCampaignImage campaignImage = QCampaignImage.campaignImage;

    private final UserRepository userRepository;


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
                        .and(campaignImage.displayOrder.eq(1))
                        .and(campaignImage.imageType.eq((TOP))))
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
                        .and(campaignImage.displayOrder.eq(1))
                        .and(campaignImage.imageType.eq((TOP))))
                .where(condition)
                .orderBy(campaign.applyDeadline.asc()) // 마감기한 얼마 남지 않은 순
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(campaign.count())
                .from(campaign)
                .innerJoin(campaignImage).on(campaignImage.campaign.eq(campaign)
                        .and(campaignImage.displayOrder.eq(1))
                        .and(campaignImage.imageType.eq((TOP))))
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

}
