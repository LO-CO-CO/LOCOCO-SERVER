package com.lokoko.domain.campaignReview.domain.repository;

import com.lokoko.domain.campaignReview.domain.entity.QCampaignReviewImage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CampaignReviewImageRepositoryImpl implements CampaignReviewImageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findImageUrlsByReviewIdOrderByDisplay(Long campaignReviewId) {
        QCampaignReviewImage img = QCampaignReviewImage.campaignReviewImage;

        return queryFactory
                .select(img.mediaFile.fileUrl)
                .from(img)
                .where(img.campaignReview.id.eq(campaignReviewId))
                .orderBy(img.displayOrder.asc())
                .fetch();
    }
}
