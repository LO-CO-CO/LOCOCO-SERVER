package com.lokoko.domain.productBrand.api.dto;

public record ProductBrandInfoProjection(
    Long productId,
	String productBrandName,
	String productName,
	String unit,
	Double averageRating,
	String imageUrl
) {
}
