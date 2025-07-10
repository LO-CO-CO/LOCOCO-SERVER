package com.lokoko.domain.review.dto.response;

import java.util.List;

public record TempResponse(
        List<ImageReviewTempResponse> imageReviews
) {
}
