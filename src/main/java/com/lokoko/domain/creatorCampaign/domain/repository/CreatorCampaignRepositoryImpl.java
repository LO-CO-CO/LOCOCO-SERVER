package com.lokoko.domain.creatorCampaign.domain.repository;

import com.lokoko.domain.brand.api.dto.request.ApplicantStatus;
import com.lokoko.domain.brand.api.dto.response.CampaignApplicantListResponse;
import com.lokoko.domain.brand.api.dto.response.CampaignApplicantResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorInfo;
import com.lokoko.domain.creator.domain.entity.QCreator;
import com.lokoko.domain.creator.domain.entity.enums.CreatorStatus;
import com.lokoko.domain.creatorCampaign.domain.entity.QCreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.creatorSocialStats.domain.entity.QCreatorSocialStats;
import com.lokoko.domain.user.api.dto.response.AdminCreator;
import com.lokoko.domain.user.api.dto.response.AdminCreatorListResponse;
import com.lokoko.domain.user.domain.entity.QUser;
import com.lokoko.domain.user.domain.entity.enums.UserStatus;
import com.lokoko.global.common.response.PageableResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CreatorCampaignRepositoryImpl implements CreatorCampaignRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QCreatorCampaign creatorCampaign = QCreatorCampaign.creatorCampaign;
    private final QCreator creator = QCreator.creator;
    private final QUser user = QUser.user;
    private final QCreatorSocialStats creatorSocialStats = QCreatorSocialStats.creatorSocialStats;
    private final QCreatorCampaign subCreatorCampaign = new QCreatorCampaign("subCreatorCampaign");


    @Override
    public CampaignApplicantListResponse findCampaignApplicants(Long brandId, Long campaignId, Pageable pageable, ApplicantStatus status) {

        StringExpression simpleStatus = new CaseBuilder()
                .when(creatorCampaign.status.eq(ParticipationStatus.PENDING))
                .then("PENDING")
                .when(creatorCampaign.status.eq(ParticipationStatus.REJECTED))
                .then("REJECTED")
                .when(creatorCampaign.status.in(ParticipationStatus.getActiveStatuses()))
                .then("APPROVED")
                .otherwise("PENDING");

        // status 파라미터에 따른 필터링 조건 생성
        BooleanExpression statusCondition = createStatusCondition(status);

        List<CampaignApplicantResponse> applicants = queryFactory
                .select(Projections.constructor(CampaignApplicantResponse.class,
                        creatorCampaign.id,
                        creator.id,
                        Projections.constructor(CreatorInfo.class,
                                creator.id,
                                user.name,
                                creator.creatorName,
                                user.profileImageUrl,
                                creator.instagramUserId,
                                creator.tikTokUserId
                        ),
                        Projections.constructor(CampaignApplicantResponse.FollowerCount.class,
                                creatorSocialStats.instagramFollower.coalesce(0),
                                creatorSocialStats.tiktokFollower.coalesce(0)
                        ),
                        JPAExpressions
                                .select(subCreatorCampaign.count().intValue())
                                .from(subCreatorCampaign)
                                .where(subCreatorCampaign.creator.id.eq(creator.id)),
                        creatorCampaign.appliedAt,
                        simpleStatus
                ))
                .from(creatorCampaign)
                .innerJoin(creatorCampaign.creator, creator)
                .innerJoin(creator.user, user)
                .leftJoin(creatorSocialStats).on(creatorSocialStats.creator.id.eq(creator.id))
                .where(
                        creatorCampaign.campaign.id.eq(campaignId),
                        creatorCampaign.campaign.brand.id.eq(brandId),
                        statusCondition  // status 필터링 조건 추가
                )
                .orderBy(creatorCampaign.appliedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // count 쿼리
        Long totalCount = queryFactory
                .select(creatorCampaign.count())
                .from(creatorCampaign)
                .where(
                        creatorCampaign.campaign.id.eq(campaignId),
                        creatorCampaign.campaign.brand.id.eq(brandId),
                        statusCondition  // count 쿼리에도 동일한 필터링 적용
                )
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;
        boolean isLast = (pageable.getOffset() + pageable.getPageSize()) >= total;

        PageableResponse pageInfo = PageableResponse.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                applicants.size(),
                isLast,
                total
        );

        return new CampaignApplicantListResponse(applicants, pageInfo);
    }

    /**
     * ApplicantStatus에 따른 필터링 조건 생성
     *
     * @param status null이면 필터링 없음, PENDING/APPROVED/REJECTED에 따라 다른 ParticipationStatus 필터링
     */
    private BooleanExpression createStatusCondition(ApplicantStatus status) {
        if (status == null) {
            return null; // 필터링 없음
        }

        return switch (status) {
            case PENDING -> creatorCampaign.status.eq(ParticipationStatus.PENDING);
            case REJECTED -> creatorCampaign.status.eq(ParticipationStatus.REJECTED);
            case APPROVED ->
                    creatorCampaign.status.in(ParticipationStatus.getActiveStatuses()); // APPROVED, ACTIVE, COMPLETED
        };
    }

    @Override
    public AdminCreatorListResponse findCreatorsByAdmin(Pageable pageable) {

        StringExpression approveStatusExpr = new CaseBuilder()
                .when(creator.creatorStatus.eq(CreatorStatus.APPROVED)).then("APPROVED")
                .otherwise("PENDING");

        List<AdminCreator> creators = queryFactory
                .select(Projections.constructor(AdminCreator.class,
                        Projections.constructor(CreatorInfo.class,
                                creator.id,
                                user.name,
                                creator.creatorName,
                                user.profileImageUrl,
                                creator.instagramUserId,
                                creator.tikTokUserId
                        ),
                        Projections.constructor(AdminCreator.FollowerCount.class,
                                creatorSocialStats.instagramFollower.coalesce(0),
                                creatorSocialStats.tiktokFollower.coalesce(0)
                        ),
                        JPAExpressions.select(subCreatorCampaign.count().intValue())
                                .from(subCreatorCampaign)
                                .where(subCreatorCampaign.creator.id.eq(creator.id)),
                        // 가입 완료 시점
                        creator.signupCompletedAt,
                        approveStatusExpr
                ))
                .from(creator)
                .innerJoin(creator.user, user)
                .leftJoin(creatorSocialStats).on(creatorSocialStats.creator.id.eq(creator.id))
                // 가입완료한 크리에이터이면서 UserStatus가 활성화 상태인 것만
                .where(creator.signupCompletedAt.isNotNull(),
                        user.status.eq(UserStatus.ACTIVE))
                .orderBy(
                        creator.signupCompletedAt.desc(),
                        user.name.asc(),
                        creator.id.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(creator.count())
                .from(creator)
                .innerJoin(creator.user, user)
                .where(creator.signupCompletedAt.isNotNull(),
                        user.status.eq(UserStatus.ACTIVE))
                .fetchOne();


        long total = totalCount != null ? totalCount : 0L;
        boolean isLast = (pageable.getOffset() + pageable.getPageSize()) >= total;

        PageableResponse pageInfo = PageableResponse.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                creators.size(),
                isLast,
                total
        );

        return new AdminCreatorListResponse(creators, total, pageInfo);
    }


}
