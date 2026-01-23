package com.lokoko.domain.product.api.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.lokoko.domain.product.domain.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
public record ProductBasicResponse(
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
        Double rating, // 별점
        @Schema(requiredMode = REQUIRED)
        Boolean isLiked // 좋아요 여부
) {
    public static ProductBasicResponse of(
            Product product,
            ProductStatsResponse summary,
            boolean isLiked
    ) {
        List<String> images = Optional.ofNullable(summary.imageUrl())
                .filter(u -> !u.isBlank())
                .map(u -> u.contains(",")
                        ? Arrays.stream(u.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList()
                        : List.of(u)
                )
                .orElseGet(List::of);

        return new ProductBasicResponse(
                product.getId(),
                images,
                product.getProductName(),
                product.getProductBrand().getBrandName(),
                product.getUnit(),
                summary.reviewCount(),
                summary.avgRating(),
                isLiked
        );
    }
}
