package com.lokoko.domain.product.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record CachedPopularProduct(
        @Schema(requiredMode = REQUIRED)
        Long productId, // 제품 id(추후 상세조회를 위해서)
        @Schema(requiredMode = REQUIRED)
        List<String> imageUrls, // 제품 이미지
        @Schema(requiredMode = REQUIRED)
        String productName,// 제품 이름
        @Schema(requiredMode = REQUIRED)
        String brandName, // 브랜드 이름
        @Schema(requiredMode = REQUIRED)
        String unit, // 제품 단위
        @Schema(requiredMode = REQUIRED)
        Long reviewCount, // 리뷰 개수
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.0")
        @Schema(requiredMode = REQUIRED)
        Double rating // 별점
) {
}
