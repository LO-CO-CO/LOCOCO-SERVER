package com.lokoko.domain.productReview.api.dto.request;

import com.lokoko.domain.productReview.domain.entity.enums.Rating;

public record RatingCount(
        Long productId,
        Rating rating,
        Long count
) {
}
