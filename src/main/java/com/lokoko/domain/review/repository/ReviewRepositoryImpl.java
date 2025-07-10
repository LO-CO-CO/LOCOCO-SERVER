package com.lokoko.domain.review.repository;

import com.lokoko.domain.image.entity.QReviewImage;
import com.lokoko.domain.product.entity.QProduct;
import com.lokoko.domain.product.entity.QProductOption;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.review.dto.ImageReviewResponse;
import com.lokoko.domain.review.dto.VideoReviewResponse;
import com.lokoko.domain.review.dto.response.ImageReviewTempResponse;
import com.lokoko.domain.review.dto.response.TempResponse;
import com.lokoko.domain.review.entity.QReview;
import com.lokoko.domain.video.entity.QReviewVideo;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReview review = QReview.review;
    private final QProduct product = QProduct.product;
    private final QReviewVideo reviewVideo = QReviewVideo.reviewVideo;
    private final QReviewImage reviewImage = QReviewImage.reviewImage;
    private final QProductOption productOption = QProductOption.productOption;

    @Override
    public Slice<VideoReviewResponse> findVideoReviewsByCategory(MiddleCategory middleCategory,
                                                                 SubCategory subCategory,
                                                                 Pageable pageable) {
        List<VideoReviewResponse> content = queryFactory
                .select(Projections.constructor(VideoReviewResponse.class,
                        review.id,
                        Expressions.constant(0),
                        product.brandName,
                        product.productName,
                        review.likeCount,
                        reviewVideo.mediaFile.fileUrl
                ))
                .from(reviewVideo)
                .innerJoin(reviewVideo.review, review)
                .innerJoin(review.product, product)
                .where(
                        categoryCondition(middleCategory, subCategory)
                )
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        for (int i = 0; i < Math.min(content.size(), pageable.getPageSize()); i++) {
            VideoReviewResponse original = content.get(i);
            content.set(i, new VideoReviewResponse(
                    original.reviewId(),
                    (int) pageable.getOffset() + i + 1,
                    original.brandName(),
                    original.productName(),
                    original.likeCount(),
                    original.url()
            ));
        }

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(content.size() - 1);
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<VideoReviewResponse> findVideoReviewsByCategory(MiddleCategory middleCategory,
                                                                 Pageable pageable) {
        return findVideoReviewsByCategory(middleCategory, null, pageable);
    }


    @Override
    public Slice<ImageReviewResponse> findImageReviewsByCategory(MiddleCategory middleCategory,
                                                                 SubCategory subCategory,
                                                                 Pageable pageable) {
        List<ImageReviewResponse> content = queryFactory
                .select(Projections.constructor(ImageReviewResponse.class,
                        review.id,
                        Expressions.constant(0),
                        product.brandName,
                        product.productName,
                        review.likeCount,
                        reviewImage.mediaFile.fileUrl
                ))
                .from(reviewImage)
                .innerJoin(reviewImage.review, review)
                .innerJoin(review.product, product)
                .where(
                        categoryCondition(middleCategory, subCategory)
                )
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        for (int i = 0; i < Math.min(content.size(), pageable.getPageSize()); i++) {
            ImageReviewResponse original = content.get(i);
            content.set(i, new ImageReviewResponse(
                    original.reviewId(),
                    (int) pageable.getOffset() + i + 1,
                    original.brandName(),
                    original.productName(),
                    original.likeCount(),
                    original.url()
            ));
        }

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(content.size() - 1);
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }


    @Override
    public Slice<ImageReviewResponse> findImageReviewsByCategory(MiddleCategory middleCategory,
                                                                 Pageable pageable) {
        return findImageReviewsByCategory(middleCategory, null, pageable);
    }


    private BooleanExpression categoryCondition(MiddleCategory middleCategory, SubCategory subCategory) {
        BooleanExpression condition = product.middleCategory.eq(middleCategory);

        if (subCategory != null) {
            condition = condition.and(product.subCategory.eq(subCategory));
        }

        return condition;
    }

    @Override
    public TempResponse findByProductId(Long productId) {

        NumberExpression<Integer> ratingAsInt =
                review.rating
                        .stringValue()
                        .when("ONE").then(1)
                        .when("TWO").then(2)
                        .when("THREE").then(3)
                        .when("FOUR").then(4)
                        .when("FIVE").then(5)
                        .otherwise(0);

        Double avgRating = queryFactory
                .select(ratingAsInt.avg())
                .from(review)
                .join(review.productOption, productOption)
                .join(productOption.product, product)
                .where(product.id.eq(productId))
                .fetchOne();

        List<Tuple> result = queryFactory
                .select(
                        review.id, // 리뷰 id
                        review.modifiedAt, // 유저가 마지막으로 리뷰 작성한 시간
                        review.receiptUploaded, // 영수증 업로드 여부
                        review.positiveContent, // 긍정 리뷰 내용
                        review.negativeContent, // 부정 리뷰 내용
                        review.author.nickname, // 리뷰 작성자 닉네임
                        review.rating,  // 리뷰 평점
                        productOption.optionName, // 상품 옵션 이름
                        reviewImage.mediaFile.fileUrl  // 사진 리뷰 목록
                )
                .from(review)
                .join(review.productOption, productOption)
                .join(productOption.product, product)
                .leftJoin(reviewImage).on(reviewImage.review.eq(review))
                .where(product.id.eq(productId))
                .orderBy(review.modifiedAt.desc())
                .fetch();

        Map<Long, ImageReviewTempResponse> map = new LinkedHashMap<>();

        for (Tuple tuple : result) {
            Long reviewId = tuple.get(review.id);
            String imageUrl = tuple.get(reviewImage.mediaFile.fileUrl);

            ImageReviewTempResponse dto = map.computeIfAbsent(reviewId, id ->
                    new ImageReviewTempResponse(
                            id,
                            0,
                            tuple.get(review.modifiedAt),
                            tuple.get(review.receiptUploaded),
                            tuple.get(review.positiveContent),
                            tuple.get(review.negativeContent),
                            tuple.get(review.author.nickname),
                            0.0,
                            tuple.get(productOption.optionName),
                            0,
                            new ArrayList<>()
                    )
            );

            // 이미지 URL이 있을 때만 추가
            if (imageUrl != null) {
                dto.images().add(imageUrl);
            }
        }

        List<ImageReviewTempResponse> finalResult = new ArrayList<>(map.values());

        return new TempResponse(finalResult);
    }


}

