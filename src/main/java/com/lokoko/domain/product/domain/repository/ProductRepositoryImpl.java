package com.lokoko.domain.product.domain.repository;

import java.util.ArrayList;
import java.util.List;

import com.lokoko.domain.product.api.dto.response.SimpleProductResponse;
import com.lokoko.domain.product.domain.entity.enums.ProductCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.lokoko.domain.media.image.domain.entity.QProductImage;
import com.lokoko.domain.product.domain.entity.QProduct;
import com.lokoko.domain.productBrand.api.dto.ProductBrandInfoProjection;
import com.lokoko.domain.productReview.domain.entity.QReview;
import com.lokoko.domain.productReview.domain.entity.enums.Rating;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProductRepositoryImpl implements ProductRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private static final QProduct p = QProduct.product;
	private static final QReview r = QReview.review;
	private final QProductImage productImage = QProductImage.productImage;

	@Override
	public List<SimpleProductResponse> findPopularProductsWithDetails(ProductCategory category) {

		NumberExpression<Integer> ratingValue = new CaseBuilder()
			.when(r.rating.eq(Rating.ONE)).then(1)
			.when(r.rating.eq(Rating.TWO)).then(2)
			.when(r.rating.eq(Rating.THREE)).then(3)
			.when(r.rating.eq(Rating.FOUR)).then(4)
			.when(r.rating.eq(Rating.FIVE)).then(5)
			.otherwise(0);

        BooleanExpression categoryCondition = makeCategoryCondition(category);

        return queryFactory
			.select(Projections.constructor(SimpleProductResponse.class,
				p.id,
				productImage.url,
				p.productName,
				p.productBrand.brandName,
				p.unit,
				r.id.count(),
				ratingValue.avg()
			))
			.from(p)
			.leftJoin(r).on(r.product.eq(p))
			.leftJoin(productImage).on(productImage.product.eq(p).and(productImage.isMain.eq(true)))
			.where(categoryCondition)
			.groupBy(p.id, p.productName, p.productBrand.brandName, p.unit, productImage.url)
			.orderBy(r.id.count().desc())
			.limit(4)
			.fetch();
	}

	@Override
	public List<SimpleProductResponse> findNewProductsWithDetails(ProductCategory category) {

        NumberExpression<Integer> ratingValue = new CaseBuilder()
                .when(r.rating.eq(Rating.ONE)).then(1)
                .when(r.rating.eq(Rating.TWO)).then(2)
                .when(r.rating.eq(Rating.THREE)).then(3)
                .when(r.rating.eq(Rating.FOUR)).then(4)
                .when(r.rating.eq(Rating.FIVE)).then(5)
                .otherwise(0);

        BooleanExpression categoryCondition = makeCategoryCondition(category);

        List<Long> top4Ids = queryFactory
                .select(p.id)
                .from(p)
                .where(categoryCondition)
                .orderBy(p.createdAt.desc())
                .limit(4)
                .fetch();

        if (top4Ids.isEmpty()){
            return new ArrayList<>();
        }

        return queryFactory
                .select(Projections.constructor(SimpleProductResponse.class,
                        p.id,
                        productImage.url,
                        p.productName,
                        p.productBrand.brandName,
                        p.unit,
                        r.id.count(),
                        ratingValue.avg()
                ))
                .from(p)
                .leftJoin(r).on(r.product.eq(p))
                .leftJoin(productImage).on(productImage.product.eq(p).and(productImage.isMain.eq(true)))
                .where(p.id.in(top4Ids))
                .groupBy(p.id, p.productName, p.productBrand.brandName, p.unit, productImage.url, p.createdAt)
                .orderBy(p.createdAt.desc())
                .fetch();
    }

    private BooleanExpression makeCategoryCondition(ProductCategory category) {
        return category != null ? p.productCategory.eq(category) : null;
    }

    @Override
	public Slice<ProductBrandInfoProjection> findProductsByBrandName(String productBrandName, Pageable pageable) {

		// alias는 메서드 범위에서만 쓰므로 여기서 생성하는 게 맞습니다 (static final로 공유 X)
		QProductImage mainProductImage = new QProductImage("mainProductImage");
		QProductImage anyProductImage = new QProductImage("anyProductImage");

		NumberExpression<Integer> ratingValue = new CaseBuilder()
			.when(r.rating.eq(Rating.ONE)).then(1)
			.when(r.rating.eq(Rating.TWO)).then(2)
			.when(r.rating.eq(Rating.THREE)).then(3)
			.when(r.rating.eq(Rating.FOUR)).then(4)
			.when(r.rating.eq(Rating.FIVE)).then(5)
			.otherwise(0);

		NumberExpression<Double> averageRatingExpression = ratingValue.avg();
		NumberExpression<Long> reviewCountExpression = r.id.count();

		var fallbackImageUrlSubquery = JPAExpressions
			.select(anyProductImage.url.min())
			.from(anyProductImage)
			.where(anyProductImage.product.eq(p));

		var imageUrlExpression = Expressions.stringTemplate(
			"coalesce({0}, {1})",
			mainProductImage.url,
			fallbackImageUrlSubquery
		);

		List<ProductBrandInfoProjection> content = queryFactory
			.select(Projections.constructor(
				ProductBrandInfoProjection.class,
                p.id,
				p.productBrand.brandName,
				p.productName,
				p.unit,
				averageRatingExpression,
				imageUrlExpression,
                r.id.count().coalesce(0L)
			))
			.from(p)
			.join(p.productBrand)
			.leftJoin(r).on(r.product.eq(p))
			.leftJoin(mainProductImage).on(
				mainProductImage.product.eq(p)
					.and(mainProductImage.isMain.eq(true))
			)
			.where(p.productBrand.brandName.eq(productBrandName))
			.groupBy(
				p.id,
				p.productBrand.brandName,
				p.productName,
				p.unit,
				mainProductImage.url
			)
			.orderBy(
				reviewCountExpression.desc(),
				p.createdAt.desc(),
                p.id.desc()
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1L)
			.fetch();

		boolean hasNext = content.size() > pageable.getPageSize();
		if (hasNext) {
			content.remove(content.size() - 1);
		}

		return new SliceImpl<>(content, pageable, hasNext);
	}

	@Override
	public Long countByProductBrandName(String productBrandName) {
		return queryFactory
			.select(p.count())
			.from(p)
			.join(p.productBrand)
			.where(p.productBrand.brandName.eq(productBrandName))
			.fetchOne();
	}

	@Override
	public Slice<ProductBrandInfoProjection> findProductsOrderedByRating(Pageable pageable) {

		QProductImage mainProductImage = new QProductImage("mainProductImage");
		QProductImage anyProductImage = new QProductImage("anyProductImage");

		NumberExpression<Integer> ratingValue = new CaseBuilder()
			.when(r.rating.eq(Rating.ONE)).then(1)
			.when(r.rating.eq(Rating.TWO)).then(2)
			.when(r.rating.eq(Rating.THREE)).then(3)
			.when(r.rating.eq(Rating.FOUR)).then(4)
			.when(r.rating.eq(Rating.FIVE)).then(5)
			.otherwise(0);

		NumberExpression<Double> averageRatingExpression = ratingValue.avg();
		NumberExpression<Long> reviewCountExpression = r.id.count();

		var fallbackImageUrlSubquery = JPAExpressions
			.select(anyProductImage.url.min())
			.from(anyProductImage)
			.where(anyProductImage.product.eq(p));

		var imageUrlExpression = Expressions.stringTemplate(
			"coalesce({0}, {1})",
			mainProductImage.url,
			fallbackImageUrlSubquery
		);

		List<ProductBrandInfoProjection> content = queryFactory
			.select(Projections.constructor(
				ProductBrandInfoProjection.class,
                p.id,
				p.productBrand.brandName,
				p.productName,
				p.unit,
				averageRatingExpression,
				imageUrlExpression,
                r.id.count().coalesce(0L)
			))
			.from(p)
			.join(p.productBrand)
			.leftJoin(r).on(r.product.eq(p))
			.leftJoin(mainProductImage).on(mainProductImage.product.eq(p).and(mainProductImage.isMain.eq(true)))
			.groupBy(
				p.id,
				p.productBrand.brandName,
				p.productName,
				p.unit,
				mainProductImage.url
			)
			.orderBy(
				reviewCountExpression.desc(),
				p.createdAt.desc(),
				p.id.desc()
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1L)
			.fetch();

		boolean hasNext = content.size() > pageable.getPageSize();
		if (hasNext) {
			content.remove(content.size() - 1);
		}

		return new SliceImpl<>(content, pageable, hasNext);
	}

	@Override
	public Long countAllProducts() {
		return queryFactory
			.select(p.count())
			.from(p)
			.fetchOne();
	}

    @Override
    public int countProductsByBrandName(String brandName) {
        Long count = queryFactory
                .select(p.count())
                .from(p)
                .where(brandNameCondition(brandName))
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }

    private BooleanExpression brandNameCondition(String brandName) {
        if (brandName == null || brandName.isBlank()) {
            return null;
        }
        return p.productBrand.brandName.eq(brandName);
    }
}