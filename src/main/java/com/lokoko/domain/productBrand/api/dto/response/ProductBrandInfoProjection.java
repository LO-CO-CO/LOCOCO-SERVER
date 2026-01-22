package com.lokoko.domain.productBrand.api.dto.response;

public record ProductBrandInfoProjection(
	String productBrandName,
	String productName,
	String unit,
	Double averageRating,
	String imageUrl
) {
}
