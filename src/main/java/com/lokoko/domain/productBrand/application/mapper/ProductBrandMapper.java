package com.lokoko.domain.productBrand.application.mapper;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import com.lokoko.domain.productBrand.api.dto.response.ProductBrandInfoListResponse;
import com.lokoko.domain.productBrand.api.dto.response.ProductBrandInfoProjection;
import com.lokoko.domain.productBrand.api.dto.response.ProductBrandInfoResponse;
import com.lokoko.domain.productBrand.api.dto.response.ProductBrandName;
import com.lokoko.domain.productBrand.api.dto.response.ProductBrandNameListResponse;
import com.lokoko.domain.productBrand.domain.entity.ProductBrand;
import com.lokoko.global.common.response.PageableResponse;
import com.lokoko.global.utils.RatingConverter;

@Component
public class ProductBrandMapper {

	public ProductBrandNameListResponse toBrandNameListResponse(List<ProductBrand> brands) {
		return ProductBrandNameListResponse.builder()
			.brandNames(brands.stream()
				.map(this::toBrandName)
				.toList())
			.build();
	}

	private ProductBrandName toBrandName(ProductBrand productBrand) {
		return ProductBrandName.builder()
			.productBrandId(productBrand.getId())
			.productBrandName(productBrand.getBrandName())
			.build();
	}

	public ProductBrandInfoResponse toProductBrandInfoResponse(ProductBrandInfoProjection productBrandInfoProjection) {
		double displayRating = RatingConverter.toDisplayRating(productBrandInfoProjection.averageRating());

		return ProductBrandInfoResponse.builder()
			.productBrandName(productBrandInfoProjection.productBrandName())
			.productName(productBrandInfoProjection.productName())
			.unit(productBrandInfoProjection.unit())
			.rating(displayRating)
			.imageUrl(productBrandInfoProjection.imageUrl())
			.build();
	}

	public ProductBrandInfoListResponse toProductBrandInfoList(
		Slice<ProductBrandInfoProjection> slice,
		List<ProductBrandInfoResponse> products,
		long totalElements
	) {
		PageableResponse pageInfo = PageableResponse.of(
			slice.getNumber(),
			slice.getSize(),
			slice.getNumberOfElements(),
			slice.isLast(),
			totalElements
		);

		return ProductBrandInfoListResponse.builder()
			.products(products)
			.pageInfo(pageInfo)
			.build();
	}
}
