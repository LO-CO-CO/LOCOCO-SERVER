package com.lokoko.domain.product.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import com.lokoko.domain.product.domain.entity.enums.SubCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ProductDetailResponse(
        @Schema(requiredMode = REQUIRED)
        Long productId,
        @Schema(requiredMode = REQUIRED)
        List<String> imageUrls,
        @Schema(requiredMode = REQUIRED)
        List<ProductOptionResponse> productOptions,
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
        Boolean isLiked,
        @Schema(requiredMode = REQUIRED)
        Long normalPrice,
        @Schema(requiredMode = REQUIRED)
        String productDetail,
        @Schema(requiredMode = REQUIRED)
        String ingredients,
        @Schema(requiredMode = REQUIRED)
        MiddleCategory middleCategory,
        @Schema(requiredMode = REQUIRED)
        SubCategory subCategory
) {
    public static ProductDetailResponse from(ProductBasicResponse response, List<ProductOptionResponse> productOptions,
                                             Product product, List<RatingPercentResponse> starPercent,
                                             Boolean isLiked) {

        return new ProductDetailResponse(
                response.productId(),
                response.imageUrls(),
                productOptions,
                response.productName(),
                response.brandName(),
                response.unit(),
                response.reviewCount(),
                response.rating(),
                starPercent,
                isLiked,
                product.getNormalPrice(),
                product.getProductDetail(),
                product.getIngredients(),
                product.getMiddleCategory(),
                product.getSubCategory()
        );
    }
}
