package com.lokoko.domain.review.api.dto.request;

import com.lokoko.domain.review.domain.entity.enums.Rating;

public record RatingCount(
        Long productId,
        Rating rating,
        Long count
) {
}
