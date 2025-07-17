package com.lokoko.domain.review.repository;

import com.lokoko.domain.image.entity.QReviewImage;
import com.lokoko.domain.like.entity.QReviewLike;
import com.lokoko.domain.product.entity.QProduct;
import com.lokoko.domain.product.entity.QProductOption;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.review.dto.request.RatingCount;
import com.lokoko.domain.review.dto.response.ImageReviewProductDetailResponse;
import com.lokoko.domain.review.dto.response.ImageReviewResponse;
import com.lokoko.domain.review.dto.response.ImageReviewsProductDetailResponse;
import com.lokoko.domain.review.dto.response.VideoReviewProductDetail;
import com.lokoko.domain.review.dto.response.VideoReviewProductDetailResponse;
import com.lokoko.domain.review.dto.response.VideoReviewResponse;
import com.lokoko.domain.review.entity.QReview;
import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.entity.enums.Role;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.domain.user.repository.UserRepository;
import com.lokoko.domain.video.entity.QReviewVideo;
import com.lokoko.global.common.response.PageableResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final QReviewLike reviewLike = QReviewLike.reviewLike;

    private final UserRepository userRepository;


    @Override
    public Slice<VideoReviewResponse> findVideoReviewsByCategory(MiddleCategory middleCategory,
                                                                 SubCategory subCategory,
                                                                 Pageable pageable) {
        List<VideoReviewResponse> content = queryFactory
                .select(Projections.constructor(VideoReviewResponse.class,
                        review.id,
                        product.brandName,
                        product.productName,
                        reviewLike.count().intValue(),
                        reviewVideo.mediaFile.fileUrl
                ))
                .from(reviewVideo)
                .innerJoin(reviewVideo.review, review)
                .innerJoin(review.product, product)
                .leftJoin(reviewLike).on(reviewLike.review.eq(review))
                .where(
                        categoryCondition(middleCategory, subCategory)
                )
                .groupBy(review.id, product.brandName, product.productName, reviewVideo.mediaFile.fileUrl)
                .orderBy(reviewLike.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

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
                        product.brandName,
                        product.productName,
                        reviewLike.count().intValue(),
                        reviewImage.mediaFile.fileUrl
                ))
                .from(reviewImage)
                .innerJoin(reviewImage.review, review)
                .innerJoin(review.product, product)
                .leftJoin(reviewLike).on(reviewLike.review.eq(review))
                .where(
                        categoryCondition(middleCategory, subCategory),
                        reviewImage.isMain.eq(true)
                )
                .groupBy(review.id, product.brandName, product.productName, reviewImage.mediaFile.fileUrl)
                .orderBy(reviewLike.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

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
    public Slice<VideoReviewResponse> findVideoReviewsByKeyword(List<String> tokens, Pageable pageable) {
        List<VideoReviewResponse> content = getVideoReviewsByKeyword(tokens, pageable);
        return createSlice(content, pageable);
    }

    @Override
    public Slice<ImageReviewResponse> findImageReviewsByKeyword(List<String> tokens, Pageable pageable) {
        List<ImageReviewResponse> content = getImageReviewsByKeyword(tokens, pageable);
        return createSlice(content, pageable);
    }

    private List<VideoReviewResponse> getVideoReviewsByKeyword(List<String> tokens, Pageable pageable) {
        return queryFactory
                .select(Projections.constructor(VideoReviewResponse.class,
                        review.id,
                        product.brandName,
                        product.productName,
                        reviewLike.count().intValue(),
                        reviewVideo.mediaFile.fileUrl
                ))
                .from(reviewVideo)
                .innerJoin(reviewVideo.review, review)
                .innerJoin(review.product, product)
                .leftJoin(reviewLike).on(reviewLike.review.eq(review))
                .where(keywordCondition(tokens))
                .groupBy(review.id, product.brandName, product.productName, reviewVideo.mediaFile.fileUrl)
                .orderBy(reviewLike.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private List<ImageReviewResponse> getImageReviewsByKeyword(List<String> tokens, Pageable pageable) {
        return queryFactory
                .select(Projections.constructor(ImageReviewResponse.class,
                        review.id,
                        product.brandName,
                        product.productName,
                        reviewLike.count().intValue(),
                        reviewImage.mediaFile.fileUrl
                ))
                .from(reviewImage)
                .innerJoin(reviewImage.review, review)
                .innerJoin(review.product, product)
                .leftJoin(reviewLike).on(reviewLike.review.eq(review))
                .where(keywordCondition(tokens),
                        reviewImage.isMain.eq(true))
                .groupBy(review.id, product.brandName, product.productName, reviewImage.mediaFile.fileUrl)
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private BooleanBuilder keywordCondition(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return new BooleanBuilder().and(Expressions.booleanTemplate("1 = 0")); // false
        }

        // 1단계: 완전 일치 검색
        String fullKeyword = String.join("", tokens);
        BooleanBuilder exactMatch = new BooleanBuilder()
                .and(product.searchToken.containsIgnoreCase(fullKeyword));

        // 완전 일치 결과가 있는지 확인
        long exactMatchCount = queryFactory
                .select(review.count())
                .from(review)
                .innerJoin(review.product, product)
                .where(exactMatch)
                .fetchOne();

        if (exactMatchCount > 0) {
            return exactMatch;
        }

        // 2단계: 모든 토큰 포함 (AND 검색)
        BooleanBuilder allTokensMatch = new BooleanBuilder();
        tokens.forEach(token ->
                allTokensMatch.and(product.searchToken.containsIgnoreCase(token))
        );

        long allTokensCount = queryFactory
                .select(review.count())
                .from(review)
                .innerJoin(review.product, product)
                .where(allTokensMatch)
                .fetchOne();

        if (allTokensCount > 0) {
            return allTokensMatch;
        }

        // 3단계: 주요 토큰 포함 (첫 번째와 마지막 토큰)
        if (tokens.size() >= 3) {
            BooleanBuilder majorTokensMatch = new BooleanBuilder()
                    .and(product.searchToken.containsIgnoreCase(tokens.get(0)))
                    .and(product.searchToken.containsIgnoreCase(tokens.get(tokens.size() - 1)));

            long majorTokensCount = queryFactory
                    .select(review.count())
                    .from(review)
                    .innerJoin(review.product, product)
                    .where(majorTokensMatch)
                    .fetchOne();

            if (majorTokensCount > 0) {
                return majorTokensMatch;
            }
        }

        // 4단계: 일부 토큰 포함 (OR 검색)
        BooleanBuilder anyTokenMatch = new BooleanBuilder();
        tokens.forEach(token ->
                anyTokenMatch.or(product.searchToken.containsIgnoreCase(token))
        );

        return anyTokenMatch;
    }


    private <T> Slice<T> createSlice(List<T> content, Pageable pageable) {
        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(content.size() - 1);
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public List<RatingCount> countByProductIdsAndRating(List<Long> productIds) {
        List<Tuple> tuples = queryFactory
                .select(
                        review.product.id,
                        review.rating,
                        review.id.count()
                )
                .from(review)
                .where(review.product.id.in(productIds))
                .groupBy(review.product.id, review.rating)
                .fetch();

        return tuples.stream()
                .map(t -> new RatingCount(
                        t.get(product.id),
                        t.get(review.rating),
                        t.get(review.id.count())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ImageReviewsProductDetailResponse findImageReviewsByProductId(Long productId, Long userId,
                                                                         Pageable pageable) {

        boolean isAdmin = validateAdmin(userId);

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
                .leftJoin(review.productOption, productOption)
                .join(review.product, product)
                .where(product.id.eq(productId))
                .fetchOne();

        List<Long> reviewIds = queryFactory
                .select(review.id)
                .from(review)
                .where(review.product.id.eq(productId))
                .orderBy(review.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (reviewIds.isEmpty()) {
            return new ImageReviewsProductDetailResponse(
                    isAdmin,
                    List.of(),
                    PageableResponse.builder()
                            .pageNumber(pageable.getPageNumber())
                            .pageSize(pageable.getPageSize())
                            .numberOfElements(0)
                            .isLast(true)
                            .build()
            );
        }

        // 현재 조회중인 사용자가 좋아요 누른 리뷰 id
        Set<Long> likedReviewIds = (userId != null) ? queryFactory
                .select(reviewLike.review.id)
                .from(reviewLike)
                .where(
                        reviewLike.user.id.eq(userId)
                                .and(reviewLike.review.id.in(reviewIds))
                )
                .fetch()
                .stream().collect(Collectors.toSet())
                : Collections.emptySet();

        Map<Long, Long> likeCounts = queryFactory
                .select(reviewLike.review.id, reviewLike.id.count())
                .from(reviewLike)
                .where(reviewLike.review.id.in(reviewIds))
                .groupBy(reviewLike.review.id)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(reviewLike.review.id),
                        tuple -> tuple.get(reviewLike.id.count())
                ));

        List<Tuple> tuples = queryFactory
                .select(
                        review.id,
                        review.modifiedAt,
                        review.receiptUploaded,
                        review.positiveContent,
                        review.negativeContent,
                        review.author.profileImageUrl,
                        review.author.nickname,
                        review.author.id,
                        ratingAsInt,
                        productOption.optionName,
                        reviewImage.mediaFile.fileUrl
                )
                .from(review)
                .leftJoin(review.productOption, productOption)
                .join(review.product, product)
                .leftJoin(reviewImage).on(reviewImage.review.eq(review))
                .where(review.id.in(reviewIds))
                .orderBy(review.modifiedAt.desc())
                .fetch();

        Map<Long, ImageReviewProductDetailResponse> map = new LinkedHashMap<>();
        for (Tuple t : tuples) {
            Long id = t.get(review.id);
            ImageReviewProductDetailResponse dto = map.computeIfAbsent(id, k -> {
                boolean isMine = (userId != null && userId.equals(t.get(review.author.id)));
                boolean isLiked = likedReviewIds.contains(k);
                return new ImageReviewProductDetailResponse(
                        k,
                        t.get(review.modifiedAt),
                        t.get(review.receiptUploaded),
                        t.get(review.positiveContent),
                        t.get(review.negativeContent),
                        t.get(review.author.profileImageUrl),
                        t.get(review.author.nickname),
                        t.get(review.author.id),
                        t.get(ratingAsInt).doubleValue(),
                        t.get(productOption.optionName),
                        likeCounts.getOrDefault(k, 0L).intValue(),
                        new ArrayList<>(),
                        isLiked,
                        isMine
                );
            });
            String img = t.get(reviewImage.mediaFile.fileUrl);
            if (img != null) {
                dto.images().add(img);
            }
        }

        List<ImageReviewProductDetailResponse> results = reviewIds.stream()
                .filter(map::containsKey)
                .map(map::get)
                .toList();

        boolean isLast = (pageable.getOffset() + pageable.getPageSize()) >= totalCount;
        PageableResponse pageInfo = PageableResponse.builder()
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .numberOfElements(results.size())
                .isLast(isLast)
                .build();

        return new ImageReviewsProductDetailResponse(isAdmin, results, pageInfo);
    }

    private boolean validateAdmin(Long userId) {
        boolean isAdmin = false;

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(UserNotFoundException::new);

            if (user.getRole() == Role.ADMIN) {
                isAdmin = true;
            }
        }
        return isAdmin;
    }

    @Override
    public VideoReviewProductDetailResponse findVideoReviewsByProductId(Long productId) {
        List<VideoReviewProductDetail> results = queryFactory
                .select(Projections.constructor(VideoReviewProductDetail.class,
                        review.id,
                        product.brandName,
                        product.productName,
                        reviewLike.count().intValue(),
                        reviewVideo.mediaFile.fileUrl
                ))
                .from(reviewVideo)
                .join(reviewVideo.review, review)
                .join(review.product, product)
                .leftJoin(reviewLike).on(reviewLike.review.eq(review))
                .where(product.id.eq(productId))
                .groupBy(review.id, product.brandName, product.productName, reviewVideo.mediaFile.fileUrl)
                .orderBy(reviewLike.count().desc(),
                        review.rating.desc())
                .limit(10)
                .fetch();

        return new VideoReviewProductDetailResponse(results);
    }

}

