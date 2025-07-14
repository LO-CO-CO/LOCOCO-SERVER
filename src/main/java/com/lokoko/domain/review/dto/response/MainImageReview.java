package com.lokoko.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MainImageReview(

        @Schema(description = "리뷰 아이디")
        Long reviewId,

        @Schema(description = "브랜드 이름")
        String brandName,

        @Schema(description = "상품 이름")
        String productName,

        @Schema(description = "리뷰 좋아요 개수")
        int likeCount,

        @Schema(description = "리뷰 순위")
        int rank,

        @Schema(description = "리뷰 이미지")
        String reviewImage
) {
}


