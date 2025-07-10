package com.lokoko.domain.review.repository;

import com.lokoko.domain.image.entity.QReviewImage;
import com.lokoko.domain.product.entity.QProduct;
import com.lokoko.domain.product.entity.QProductOption;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.review.dto.ImageReviewResponse;
import com.lokoko.domain.review.dto.VideoReviewResponse;
import com.lokoko.domain.review.dto.response.ImageReviewProductDetailResponse;
import com.lokoko.domain.review.dto.response.ImageReviewsProductDetailResponse;
import com.lokoko.domain.review.entity.QReview;
import com.lokoko.domain.video.entity.QReviewVideo;
import com.lokoko.global.common.response.PageableResponse;
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
    public ImageReviewsProductDetailResponse findImageReviewsByProductId(Long productId, Pageable pageable) {

        NumberExpression<Integer> ratingAsInt =
                review.rating
                        .stringValue()
                        .when("ONE").then(1)
                        .when("TWO").then(2)
                        .when("THREE").then(3)
                        .when("FOUR").then(4)
                        .when("FIVE").then(5)
                        .otherwise(0);

        Long totalCount = queryFactory
                .select(review.id.countDistinct())
                .from(review)
                .join(review.productOption, productOption)
                .join(productOption.product, product)
                .where(product.id.eq(productId))
                .fetchOne();

        List<Long> reviewIds = queryFactory
                .select(review.id)
                .from(review)
                .join(review.productOption, productOption)
                .join(productOption.product, product)
                .where(product.id.eq(productId))
                .orderBy(review.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (reviewIds.isEmpty()) {
            return new ImageReviewsProductDetailResponse(
                    List.of(),
                    PageableResponse.builder()
                            .pageNumber(pageable.getPageNumber())
                            .pageSize(pageable.getPageSize())
                            .numberOfElements(0)
                            .isLast(true)
                            .build()
            );
        }

        List<Tuple> result = queryFactory
                .select(
                        review.id,
                        review.modifiedAt,
                        review.receiptUploaded,
                        review.positiveContent,
                        review.negativeContent,
                        review.author.nickname,
                        ratingAsInt,
                        productOption.optionName,
                        reviewImage.mediaFile.fileUrl
                )
                .from(review)
                .join(review.productOption, productOption)
                .join(productOption.product, product)
                .leftJoin(reviewImage).on(reviewImage.review.eq(review))
                .where(review.id.in(reviewIds))
                .orderBy(review.modifiedAt.desc())
                .fetch();

        Map<Long, ImageReviewProductDetailResponse> map = new LinkedHashMap<>();

        for (Tuple tuple : result) {
            Long reviewId = tuple.get(review.id);
            String imageUrl = tuple.get(reviewImage.mediaFile.fileUrl);
            Integer individualRating = tuple.get(ratingAsInt);

            ImageReviewProductDetailResponse dto = map.computeIfAbsent(reviewId, id ->
                    new ImageReviewProductDetailResponse(
                            id,
                            tuple.get(review.modifiedAt),
                            tuple.get(review.receiptUploaded),
                            tuple.get(review.positiveContent),
                            tuple.get(review.negativeContent),
                            tuple.get(review.author.nickname),
                            individualRating != null ? individualRating.doubleValue() : 0.0,
                            tuple.get(productOption.optionName),
                            0,
                            new ArrayList<>()
                    )
            );

            if (imageUrl != null) {
                dto.images().add(imageUrl);
            }
        }

        List<ImageReviewProductDetailResponse> results = reviewIds.stream()
                .map(map::get)
                .toList();

        boolean isLast = (pageable.getOffset() + pageable.getPageSize()) >= totalCount;
        PageableResponse pageInfo = PageableResponse.builder()
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .numberOfElements(results.size())
                .isLast(isLast)
                .build();

        return new ImageReviewsProductDetailResponse(results, pageInfo);
    }
}

