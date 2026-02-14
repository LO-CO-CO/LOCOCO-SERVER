package com.lokoko.domain.product.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lokoko.domain.product.domain.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record ProductDetailResponse(
        @Schema(requiredMode = REQUIRED)
        Long productId,
        @Schema(requiredMode = REQUIRED)
        List<String> imageUrls,
        @Schema(requiredMode = REQUIRED)
        String productName,
        @Schema(requiredMode = REQUIRED)
        String brandName,
        @Schema(requiredMode = REQUIRED)
        String unit,
        @Schema(requiredMode = REQUIRED)
        Long reviewCount,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.0")
        @Schema(requiredMode = REQUIRED)
        Double rating,
        @Schema(requiredMode = REQUIRED)
        List<RatingPercentResponse> starPercent,
        @Schema(requiredMode = REQUIRED)
        BigDecimal normalPrice,
        @Schema(requiredMode = REQUIRED)
        String productDetail,
        @Schema(requiredMode = REQUIRED)
        String ingredients
) {
    public static ProductDetailResponse from(ProductBasicResponse response, List<ProductOptionResponse> productOptions,
                                             Product product, List<RatingPercentResponse> starPercent) {

        return new ProductDetailResponse(
                response.productId(),
                response.imageUrls(),
                response.productName(),
                response.brandName(),
                response.unit(),
                response.reviewCount(),
                response.rating(),
                starPercent,
                product.getNormalPrice(),
                product.getProductDetail(),
                product.getIngredients()
        );
    }
}
