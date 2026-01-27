package com.lokoko.domain.media.video.domain.repository;

import com.lokoko.domain.like.domain.entity.QReviewLikeCount;
import com.lokoko.domain.media.video.domain.entity.QReviewVideo;
import com.lokoko.domain.product.domain.entity.QProduct;
import com.lokoko.domain.productReview.api.dto.response.MainVideoReview;
import com.lokoko.domain.productReview.domain.entity.QReview;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewVideoRepositoryImpl implements ReviewVideoRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private static final QReviewVideo reviewVideo = QReviewVideo.reviewVideo;
    private static final QReview review = QReview.review;
    private static final QProduct product = QProduct.product;
    private static final QReviewLikeCount reviewLikeCount = QReviewLikeCount.reviewLikeCount;


    @Override
    public List<MainVideoReview> findMainVideoReviewSorted() {
        return queryFactory
                .select(Projections.constructor(MainVideoReview.class,
                        review.id,
                        product.id,
                        product.productBrand.brandName,
                        product.productName,
                        reviewLikeCount.likeCount.coalesce(0L),
                        // 일단 여기서 rank 0, service에서 추가
                        Expressions.constant(0),
                        reviewVideo.mediaFile.fileUrl
                ))
                .from(reviewVideo)
                .join(reviewVideo.review, review)
                .join(review.product, product)
                .leftJoin(reviewLikeCount).on(reviewLikeCount.reviewId.eq(review.id))
                .where(reviewVideo.displayOrder.eq(0))
                .orderBy(reviewLikeCount.likeCount.desc())
                .limit(4)
                .fetch();
    }

}
