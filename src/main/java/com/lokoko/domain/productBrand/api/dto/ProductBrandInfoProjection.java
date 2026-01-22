package com.lokoko.domain.productBrand.api.dto;

public record ProductBrandInfoProjection(
	String productBrandName,
	String productName,
	String unit,
	Double averageRating,
	String imageUrl
) {
}
