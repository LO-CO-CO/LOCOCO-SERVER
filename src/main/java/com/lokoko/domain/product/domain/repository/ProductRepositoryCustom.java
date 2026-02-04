package com.lokoko.domain.product.domain.repository;

import java.util.List;

import com.lokoko.domain.product.api.dto.response.SimpleProductResponse;
import com.lokoko.domain.product.domain.entity.enums.ProductCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.lokoko.domain.productBrand.api.dto.ProductBrandInfoProjection;

public interface ProductRepositoryCustom {

	List<SimpleProductResponse> findPopularProductsWithDetails(ProductCategory productCategory);

	List<SimpleProductResponse> findNewProductsWithDetails(ProductCategory productCategory);

	Slice<ProductBrandInfoProjection> findProductsByBrandName(String productBrandName, Pageable pageable);

	Long countByProductBrandName(String productBrandName);

	Slice<ProductBrandInfoProjection> findProductsOrderedByRating(Pageable pageable);

	Long countAllProducts();

    int countProductsByBrandName(String brandName);
}
