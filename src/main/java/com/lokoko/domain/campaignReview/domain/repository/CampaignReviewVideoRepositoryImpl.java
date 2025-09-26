package com.lokoko.domain.campaignReview.domain.repository;

import com.lokoko.domain.campaignReview.domain.entity.QCampaignReviewVideo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CampaignReviewVideoRepositoryImpl implements CampaignReviewVideoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findVideoUrlsByReviewIdOrderByDisplay(Long campaignReviewId) {
        QCampaignReviewVideo vid = QCampaignReviewVideo.campaignReviewVideo;

        return queryFactory
                .select(vid.mediaFile.fileUrl)
                .from(vid)
                .where(vid.campaignReview.id.eq(campaignReviewId))
                .orderBy(vid.displayOrder.asc())
                .fetch();
    }
}
