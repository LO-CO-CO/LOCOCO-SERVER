package com.lokoko.domain.creatorCampaign.domain.repository;

import com.lokoko.domain.brand.api.dto.response.CampaignApplicantListResponse;
import com.lokoko.domain.brand.api.dto.response.CampaignApplicantResponse;
import com.lokoko.domain.creator.domain.entity.QCreator;
import com.lokoko.domain.creatorCampaign.domain.entity.QCreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import com.lokoko.domain.creatorSocialStats.domain.entity.QCreatorSocialStats;
import com.lokoko.domain.user.domain.entity.QUser;
import com.lokoko.global.common.response.PageableResponse;
import com.querydsl.core.types.Projections;
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
public class CreatorCampaignRepositoryImpl implements CreatorCampaignRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QCreatorCampaign creatorCampaign = QCreatorCampaign.creatorCampaign;
    private final QCreator creator = QCreator.creator;
    private final QUser user = QUser.user;
    private final QCreatorSocialStats creatorSocialStats =  QCreatorSocialStats.creatorSocialStats;
    private final QCreatorCampaign subCreatorCampaign = new QCreatorCampaign("subCreatorCampaign");


    @Override
    public CampaignApplicantListResponse findCampaignApplicants(Long brandId, Long campaignId, Pageable pageable) {

        StringExpression simpleStatus = new CaseBuilder()
                .when(creatorCampaign.status.eq(ParticipationStatus.PENDING))
                    .then("PENDING")
                .when(creatorCampaign.status.eq(ParticipationStatus.REJECTED))
                    .then("REJECTED")
                .when(creatorCampaign.status.in(ParticipationStatus.getApprovedStatuses()))
                    .then("APPROVED")
                .otherwise("PENDING");

        List<CampaignApplicantResponse> applicants = queryFactory
                .select(Projections.constructor(CampaignApplicantResponse.class,
                        creatorCampaign.id,
                        creator.id,
                        user.profileImageUrl,
                        user.name,
                        creator.creatorName,
                        creatorSocialStats.instagramFollower,
                        creatorSocialStats.tiktokFollower,
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
                .innerJoin(creatorSocialStats).on(creatorSocialStats.creator.id.eq(creator.id))
                .where(
                        creatorCampaign.campaign.id.eq(campaignId),
                        creatorCampaign.campaign.brand.id.eq(brandId)
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
                        creatorCampaign.campaign.brand.id.eq(brandId)
                )
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;
        boolean isLast = (pageable.getOffset() + pageable.getPageSize()) >= total;

        PageableResponse pageInfo = new PageableResponse(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                applicants.size(),
                isLast
        );

        return new CampaignApplicantListResponse(applicants, pageInfo);
    }
}
