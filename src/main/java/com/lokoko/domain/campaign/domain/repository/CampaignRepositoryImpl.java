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
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
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
    public MainPageUpcomingCampaignListResponse findUpcomingCampaignsInMainPage(LanguageFilter lang, CampaignProductTypeFilter category, PageRequest pageable) {

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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long totalCount = queryFactory
                .select(campaign.count())
                .from(campaign)
                .where(campaign.campaignStatus.eq(CampaignStatus.OPEN_RESERVED).and(languageAndCategoryCondition))
                .fetchOne();

        boolean isLast = (pageable.getOffset() + pageable.getPageSize()) >= totalCount;

        PageableResponse pageInfo = new PageableResponse(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                upcomingCampaigns.size(),
                isLast);


        return new MainPageUpcomingCampaignListResponse(upcomingCampaigns, pageInfo);

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

        List<Tuple> results = queryFactory
                .select(
                        campaign.id,
                        campaign.campaignType,
                        campaign.language,
                        campaign.brand.brandName,
                        campaignImage.mediaFile.fileUrl,
                        campaign.title,
                        campaign.applicantNumber,
                        campaign.recruitmentNumber,
                        campaign.applyStartDate,
                        campaign.applyDeadline,
                        campaign.creatorAnnouncementDate,
                        campaign.reviewSubmissionDeadline,
                        campaign.campaignStatus
                )
                .from(campaign)
                .innerJoin(campaignImage).on(campaignImage.campaign.eq(campaign)
                        .and(campaignImage.displayOrder.eq(1))
                        .and(campaignImage.imageType.eq((TOP))))
                .where(condition)
                .orderBy(campaign.applyDeadline.asc()) // 마감기한 얼마 남지 않은 순
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<MainPageCampaignResponse> campaignList = results.stream()
                .map(tuple -> {
                    String chipStatus = determineChipStatus(
                            tuple.get(campaign.applyStartDate),
                            tuple.get(campaign.applyDeadline)
                    );

                    return new MainPageCampaignResponse(
                            tuple.get(campaign.id),
                            tuple.get(campaign.campaignType),
                            tuple.get(campaign.language),
                            tuple.get(campaign.brand.brandName),
                            tuple.get(campaignImage.mediaFile.fileUrl),
                            tuple.get(campaign.title),
                            tuple.get(campaign.applicantNumber),
                            tuple.get(campaign.recruitmentNumber),
                            tuple.get(campaign.reviewSubmissionDeadline),
                            chipStatus
                    );
                })
                .toList();

        Long totalCount = queryFactory
                .select(campaign.count())
                .from(campaign)
                .where(condition)
                .fetchOne();

        boolean isLast = (pageable.getOffset() + pageable.getPageSize()) >= totalCount;

        PageableResponse pageInfo = new PageableResponse(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                campaignList.size(),
                isLast);

        return new MainPageCampaignListResponse(campaignList, pageInfo);
    }

    private String determineChipStatus(Instant applyStartDate, Instant applyDeadline) {
        Instant now = Instant.now();

        if (now.isAfter(applyStartDate) && now.isBefore(applyDeadline)) {
            return CampaignChipStatus.DEFAULT.getDisplayName();
        } else if (now.isAfter(applyDeadline)) {
            return CampaignChipStatus.DISABLED.getDisplayName();
        }

        return null;
    }

    /**
     * 실시간 상태 계산 메서드
     */
    private CampaignStatus calculateRealTimeStatus(CampaignStatus dbStatus, Instant applyStartDate,
                                                   Instant applyDeadline, Instant creatorAnnouncementDate,
                                                   Instant reviewSubmissionDeadline, Instant now) {
        if (dbStatus == CampaignStatus.DRAFT) {
            return CampaignStatus.DRAFT;
        }
        if (dbStatus == CampaignStatus.WAITING_APPROVAL) {
            return CampaignStatus.WAITING_APPROVAL;
        }
        if (now.isBefore(applyStartDate)) {
            return CampaignStatus.OPEN_RESERVED;
        }
        if (now.isBefore(applyDeadline)) {
            return CampaignStatus.RECRUITING;
        }
        if (now.isBefore(creatorAnnouncementDate)) {
            return CampaignStatus.RECRUITMENT_CLOSED;
        }
        if (now.isBefore(reviewSubmissionDeadline)) {
            return CampaignStatus.IN_REVIEW;
        }

        return CampaignStatus.COMPLETED;
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

}
