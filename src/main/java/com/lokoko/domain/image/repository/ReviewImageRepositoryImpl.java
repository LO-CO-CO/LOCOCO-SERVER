package com.lokoko.domain.image.repository;

import com.lokoko.domain.image.entity.QReviewImage;
import com.lokoko.domain.like.entity.QReviewLike;
import com.lokoko.domain.product.domain.entity.QProduct;
import com.lokoko.domain.review.api.dto.response.MainImageReview;
import com.lokoko.domain.review.entity.QReview;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewImageRepositoryImpl implements ReviewImageRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final QReviewImage reviewImage = QReviewImage.reviewImage;
    private static final QReview review = QReview.review;
    private static final QProduct product = QProduct.product;
    private static final QReviewLike reviewLike = QReviewLike.reviewLike;

    @Override
    public List<MainImageReview> findMainImageReviewSorted() {
        return queryFactory
                .select(Projections.constructor(MainImageReview.class,
                        review.id,
                        product.id,
                        product.brandName,
                        product.productName,
                        reviewLike.count().intValue(),
                        // 일단 여기서 rank 0, service에서 추가
                        Expressions.constant(0),
                        reviewImage.mediaFile.fileUrl
                ))
                .from(reviewImage)
                .join(reviewImage.review, review)
                .join(review.product, product)
                .leftJoin(reviewLike).on(reviewLike.review.eq(review))
                .where(reviewImage.displayOrder.eq(0))
                .groupBy(review.id, product.brandName, product.productName, reviewImage.mediaFile.fileUrl)
                .orderBy(reviewLike.count().desc(), review.rating.desc())
                .limit(4)
                .fetch();
    }

}