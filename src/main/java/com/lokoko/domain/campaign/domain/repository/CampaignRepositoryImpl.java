package com.lokoko.domain.campaign.domain.repository;

import com.lokoko.domain.campaign.api.dto.response.MainPageCampaignListResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageCampaignResponse;
import com.lokoko.domain.campaign.domain.entity.QCampaign;
import com.lokoko.domain.campaign.domain.entity.enums.*;
import com.lokoko.domain.image.domain.entity.QCampaignImage;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.global.common.response.PageableResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.List;

import static com.lokoko.domain.image.domain.entity.enums.ImageType.TOP;

@Repository
@RequiredArgsConstructor
public class CampaignRepositoryImpl implements CampaignRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QCampaign campaign = QCampaign.campaign;
    private final QCampaignImage campaignImage = QCampaignImage.campaignImage;

    private final UserRepository userRepository;


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

    @Override
    public MainPageCampaignListResponse findCampaignsInMainPage(Long userId, LanguageFilter lang, CampaignProductTypeFilter category, Pageable pageable) {

        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        BooleanExpression langCondition = buildLanguageCondition(lang);
        BooleanExpression categoryCondition = buildCategoryCondition(category);
        BooleanExpression visibilityCondition = buildVisibilityCondition(user);

        BooleanExpression condition = combineConditions(
                langCondition,
                categoryCondition,
                visibilityCondition
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
                .leftJoin(campaignImage).on(campaignImage.campaign.eq(campaign)
                        .and(campaignImage.displayOrder.eq(1))
                        .and(campaignImage.imageType.eq((TOP))))
                .where(condition)
                .orderBy(campaign.applyDeadline.asc()) // 마감기한 얼마 남지 않은 순
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Instant now = Instant.now();

        List<MainPageCampaignResponse> campaignList = results.stream()
                .map(tuple -> {
                    CampaignStatus dbStatus = tuple.get(campaign.campaignStatus);
                    CampaignStatus calculatedStatus = calculateRealTimeStatus(
                            dbStatus,
                            tuple.get(campaign.applyStartDate),
                            tuple.get(campaign.applyDeadline),
                            tuple.get(campaign.creatorAnnouncementDate),
                            tuple.get(campaign.reviewSubmissionDeadline),
                            now
                    );

                    return new AbstractMap.SimpleEntry<>(tuple, calculatedStatus);
                })
                // DRAFT와 WAITING_APPROVAL 는 제외한다.
                .filter(entry -> {
                    CampaignStatus status = entry.getValue();
                    return status != CampaignStatus.DRAFT && status != CampaignStatus.WAITING_APPROVAL;
                })
                .map(entry -> {
                    Tuple tuple = entry.getKey();
                    return new MainPageCampaignResponse(
                            tuple.get(campaign.id),
                            tuple.get(campaign.campaignType),
                            tuple.get(campaign.language),
                            tuple.get(campaign.brand.brandName),
                            tuple.get(campaignImage.mediaFile.fileUrl),
                            tuple.get(campaign.title),
                            tuple.get(campaign.applicantNumber),
                            tuple.get(campaign.recruitmentNumber),
                            tuple.get(campaign.applyStartDate),
                            tuple.get(campaign.reviewSubmissionDeadline)
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
