package com.lokoko.domain.review.dto.response;

import com.lokoko.global.common.response.PageableResponse;
import java.util.List;

public record ImageReviewsProductDetailResponse(
        List<ImageReviewProductDetailResponse> imageReviews,
        PageableResponse pageInfo
) {
}
