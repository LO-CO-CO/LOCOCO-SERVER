package com.lokoko.domain.product.api.dto;

public record ReviewStats(
        long reviewCount,
        long weightedSum,
        double avgRating
) {
}
