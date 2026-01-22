package com.lokoko.domain.product.domain.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.lokoko.domain.product.api.dto.NewProductProjection;
import com.lokoko.domain.product.api.dto.PopularProductProjection;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import com.lokoko.domain.product.domain.entity.enums.SubCategory;
import com.lokoko.domain.productBrand.api.dto.ProductBrandInfoProjection;

public interface ProductRepositoryCustom {
	Slice<Product> searchByTokens(List<String> tokens, Pageable pageable);

	Slice<Product> findProductsByPopularityAndRating(MiddleCategory category, Pageable pageable);

	Slice<Product> findProductsByPopularityAndRating(MiddleCategory category, SubCategory subCategory,
		Pageable pageable);

	Slice<PopularProductProjection> findPopularProductsWithDetails(MiddleCategory middleCategory,
		Pageable pageable);

	Slice<NewProductProjection> findNewProductsWithDetails(
		MiddleCategory category,
		Pageable pageable
	);

	Slice<ProductBrandInfoProjection> getProductsByBrandName(String productBrandName, Pageable pageable);

	Long countByProductBrandName(String productBrandName);
}
