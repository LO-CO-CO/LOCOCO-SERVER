package com.lokoko.domain.product.domain.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.lokoko.domain.like.domain.entity.QProductLike;
import com.lokoko.domain.media.image.domain.entity.QProductImage;
import com.lokoko.domain.product.api.dto.NewProductProjection;
import com.lokoko.domain.product.api.dto.PopularProductProjection;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.entity.QProduct;
import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import com.lokoko.domain.product.domain.entity.enums.SubCategory;
import com.lokoko.domain.product.domain.entity.enums.Tag;
import com.lokoko.domain.productBrand.api.dto.ProductBrandInfoProjection;
import com.lokoko.domain.productReview.domain.entity.QReview;
import com.lokoko.domain.productReview.domain.entity.enums.Rating;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 1단계 : 완전 일치 검색 - 모든 토큰을 연결한 문자열로 검색한다. 2단계 : 모든 토큰 포함(AND 검색) - 각 토큰이 모두 포함된 경우 3단계 : 주요 토큰 포함 - 첫 번째 토큰(일반적으로 브랜드명)
 * 과 마지막 토큰 포함하는 경우 4단계 : 일부 토큰 포함 (or 검색) - 하나라도 포함되면 조회
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ProductRepositoryImpl implements ProductRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private static final QProduct p = QProduct.product;
	private static final QReview r = QReview.review;
	private final QProductImage productImage = QProductImage.productImage;
	private final QProductLike productLike = QProductLike.productLike;

	/**
	 * 주어진 토큰 리스트를 기반으로 상품 검색 단계적으로 검색이 수행되고, 각 단계에서 결과가 존재하면 바로 반환
	 *
	 * @param tokens 형태소 분석기를 통해서 만들어진 검색어 토큰 리스트
	 * @return 검색 조건에 부합하는 Product List
	 */

	@Override
	public Slice<Product> searchByTokens(List<String> tokens, Pageable pageable) {
		List<Product> allMatches;
		if (tokens.isEmpty()) {
			allMatches = List.of();
		} else {
			NumberExpression<Long> reviewCount = r.id.count();
			NumberExpression<Integer> ratingValue = new CaseBuilder()
				.when(r.rating.eq(Rating.ONE)).then(1)
				.when(r.rating.eq(Rating.TWO)).then(2)
				.when(r.rating.eq(Rating.THREE)).then(3)
				.when(r.rating.eq(Rating.FOUR)).then(4)
				.when(r.rating.eq(Rating.FIVE)).then(5)
				.otherwise(0);
			NumberExpression<Double> ratingAvgExpr = ratingValue.avg();

			BooleanBuilder finalCondition = buildSearchCondition(tokens);

			allMatches = queryFactory
				.select(p)
				.from(p)
				.leftJoin(r).on(r.product.eq(p))
				.where(finalCondition)
				.groupBy(p.id)
				.orderBy(reviewCount.desc(), ratingAvgExpr.desc())
				.fetch();
		}

		int offset = (int)pageable.getOffset();
		int limit = pageable.getPageSize();
		List<Product> content;
		boolean hasNext = false;

		if (offset >= allMatches.size()) {
			content = List.of();
		} else {
			int toIndex = Math.min(offset + limit, allMatches.size());
			content = allMatches.subList(offset, toIndex);
			hasNext = allMatches.size() > toIndex;
		}

		return new SliceImpl<>(content, pageable, hasNext);
	}

	/**
	 * 검색 토큰 리스트를 기반으로 단계적 검색 조건을 구성 1단계: 완전 일치 검색 2단계: 모든 토큰 포함 (AND 검색) 3단계: 주요 토큰 포함 (첫 번째 + 마지막 토큰) 4단계: 일부 토큰 포함
	 * (OR 검색)
	 */
	private BooleanBuilder buildSearchCondition(List<String> tokens) {

		String fullKeyword = String.join("", tokens);
		BooleanBuilder exactMatch = new BooleanBuilder()
			.and(p.searchToken.containsIgnoreCase(fullKeyword));

		long exactMatchCount = queryFactory
			.selectFrom(p)
			.where(exactMatch)
			.fetchCount();

		if (exactMatchCount > 0) {
			return exactMatch;
		}

		BooleanBuilder allTokensMatch = new BooleanBuilder();
		tokens.forEach(token ->
			allTokensMatch.and(p.searchToken.containsIgnoreCase(token))
		);

		long allTokensCount = queryFactory
			.selectFrom(p)
			.where(allTokensMatch)
			.fetchCount();

		if (allTokensCount > 0) {
			return allTokensMatch;
		}

		if (tokens.size() >= 3) {
			BooleanBuilder majorTokensMatch = new BooleanBuilder()
				.and(p.searchToken.containsIgnoreCase(tokens.get(0)))
				.and(p.searchToken.containsIgnoreCase(tokens.get(tokens.size() - 1)));

			long majorTokensCount = queryFactory
				.selectFrom(p)
				.where(majorTokensMatch)
				.fetchCount();

			if (majorTokensCount > 0) {
				return majorTokensMatch;
			}
		}

		BooleanBuilder anyTokenMatch = new BooleanBuilder();
		tokens.forEach(token ->
			anyTokenMatch.or(p.searchToken.containsIgnoreCase(token))
		);

		return anyTokenMatch;
	}

	@Override
	public Slice<Product> findProductsByPopularityAndRating(
		MiddleCategory category,
		Pageable pageable
	) {
		NumberExpression<Long> reviewCount = r.id.count();
		NumberExpression<Integer> ratingValue = new CaseBuilder()
			.when(r.rating.eq(Rating.ONE)).then(1)
			.when(r.rating.eq(Rating.TWO)).then(2)
			.when(r.rating.eq(Rating.THREE)).then(3)
			.when(r.rating.eq(Rating.FOUR)).then(4)
			.when(r.rating.eq(Rating.FIVE)).then(5)
			.otherwise(0);
		NumberExpression<Double> ratingAvg = ratingValue.avg();
		List<Product> content = queryFactory
			.select(p)
			.from(p)
			.leftJoin(r).on(r.product.eq(p))
			.where(p.middleCategory.eq(category))
			.groupBy(p.id)
			.orderBy(
				reviewCount.desc(),
				ratingAvg.desc()
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		boolean hasNext = content.size() == pageable.getPageSize();
		return new SliceImpl<>(content, pageable, hasNext);
	}

	@Override
	public Slice<Product> findProductsByPopularityAndRating(
		MiddleCategory category,
		SubCategory subCategory,
		Pageable pageable
	) {
		NumberExpression<Long> reviewCount = r.id.count();
		NumberExpression<Integer> ratingValue = new CaseBuilder()
			.when(r.rating.eq(Rating.ONE)).then(1)
			.when(r.rating.eq(Rating.TWO)).then(2)
			.when(r.rating.eq(Rating.THREE)).then(3)
			.when(r.rating.eq(Rating.FOUR)).then(4)
			.when(r.rating.eq(Rating.FIVE)).then(5)
			.otherwise(0);
		NumberExpression<Double> ratingAvg = ratingValue.avg();
		BooleanExpression where = p.middleCategory.eq(category);
		if (subCategory != null) {
			where = where.and(p.subCategory.eq(subCategory));
		}
		List<Product> content = queryFactory
			.select(p)
			.from(p)
			.leftJoin(r).on(r.product.eq(p))
			.where(where)
			.groupBy(p.id)
			.orderBy(reviewCount.desc(), ratingAvg.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		boolean hasNext = content.size() == pageable.getPageSize();
		return new SliceImpl<>(content, pageable, hasNext);
	}

	@Override
	public Slice<PopularProductProjection> findPopularProductsWithDetails(
		MiddleCategory category,
		Pageable pageable) {

		NumberExpression<Integer> ratingValue = new CaseBuilder()
			.when(r.rating.eq(Rating.ONE)).then(1)
			.when(r.rating.eq(Rating.TWO)).then(2)
			.when(r.rating.eq(Rating.THREE)).then(3)
			.when(r.rating.eq(Rating.FOUR)).then(4)
			.when(r.rating.eq(Rating.FIVE)).then(5)
			.otherwise(0);

		JPAQuery<PopularProductProjection> query = queryFactory
			.select(Projections.constructor(PopularProductProjection.class,
				p.id,
				p.productName,
				p.productBrand.brandName,
				p.unit,
				r.id.count(),
				ratingValue.avg(),
				productImage.url,
				Expressions.FALSE  // 또는 Expressions.asBoolean(false)
			))
			.from(p)
			.leftJoin(r).on(r.product.eq(p))
			.leftJoin(productImage).on(productImage.product.eq(p).and(productImage.isMain.eq(true)));

		List<PopularProductProjection> content = query
			.where(p.middleCategory.eq(category))
			.groupBy(p.id, p.productName, p.productBrand.brandName, p.unit, productImage.url)
			.orderBy(r.id.count().desc(), ratingValue.avg().desc())
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
	public Slice<NewProductProjection> findNewProductsWithDetails(
		MiddleCategory category,
		Pageable pageable) {

		NumberExpression<Integer> ratingValue = new CaseBuilder()
			.when(r.rating.eq(Rating.ONE)).then(1)
			.when(r.rating.eq(Rating.TWO)).then(2)
			.when(r.rating.eq(Rating.THREE)).then(3)
			.when(r.rating.eq(Rating.FOUR)).then(4)
			.when(r.rating.eq(Rating.FIVE)).then(5)
			.otherwise(0);

		JPAQuery<NewProductProjection> query = queryFactory
			.select(Projections.constructor(NewProductProjection.class,
				p.id,
				p.productName,
				p.productBrand.brandName,
				p.unit,
				r.id.count(),
				ratingValue.avg(),
				productImage.url,
				Expressions.FALSE,
				p.createdAt
			))
			.from(p)
			.leftJoin(r).on(r.product.eq(p))
			.leftJoin(productImage).on(productImage.product.eq(p).and(productImage.isMain.eq(true)));

		List<NewProductProjection> content = query
			.where(
				p.middleCategory.eq(category)
					.and(p.tag.eq(Tag.NEW))
			)
			.groupBy(p.id, p.productName, p.productBrand.brandName, p.unit, productImage.url, p.createdAt)
			.orderBy(
				r.id.count().desc(), //  리뷰 수
				ratingValue.avg().desc(), // 별점
				p.createdAt.desc()
			)
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
				p.productBrand.brandName,
				p.productName,
				p.unit,
				averageRatingExpression,
				imageUrlExpression
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
				averageRatingExpression.desc(),
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
}