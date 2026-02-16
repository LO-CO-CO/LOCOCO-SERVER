package com.lokoko.domain.productReview.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ReviewRequest(
        @NotNull
        @Schema(description = "별점 (1~5)", example = "5")
        Integer rating,

        @NotNull
        @Size(min = 15, max = 1500)
        @Schema(description = "긍정적인 리뷰 내용 (15자 이상 1500자 이하)", example = "This product is amazing and works great on my skin!")
        String positiveComment,

        @NotNull
        @Size(min = 15, max = 1500)
        @Schema(description = "부정적인 리뷰 내용 (15자 이상 1500자 이하)", example = "The packaging could be improved for better usability.")
        String negativeComment,

        @Schema(description = "리뷰 이미지 URL 목록", example = "[\"https://example.com/image1.jpg\"]")
        List<String> mediaUrl
) {
}
