package com.lokoko.domain.product.domain.repository;

import java.util.List;

import com.lokoko.domain.product.api.dto.response.SimpleProductResponse;
import com.lokoko.domain.product.domain.entity.enums.ProductCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import com.lokoko.domain.product.domain.entity.enums.SubCategory;
import com.lokoko.domain.productBrand.api.dto.ProductBrandInfoProjection;

public interface ProductRepositoryCustom {
	Slice<Product> searchByTokens(List<String> tokens, Pageable pageable);

	Slice<Product> findProductsByPopularityAndRating(MiddleCategory category, Pageable pageable);

	Slice<Product> findProductsByPopularityAndRating(MiddleCategory category, SubCategory subCategory,
		Pageable pageable);

	List<SimpleProductResponse> findPopularProductsWithDetails(ProductCategory productCategory);

	List<SimpleProductResponse> findNewProductsWithDetails(ProductCategory productCategory);

	Slice<ProductBrandInfoProjection> findProductsByBrandName(String productBrandName, Pageable pageable);

	Long countByProductBrandName(String productBrandName);

	Slice<ProductBrandInfoProjection> findProductsOrderedByRating(Pageable pageable);

	Long countAllProducts();
}
