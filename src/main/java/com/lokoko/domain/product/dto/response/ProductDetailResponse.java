package com.lokoko.domain.product.dto.response;

import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

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
        @Schema(requiredMode = REQUIRED)
        Double rating,
        @Schema(requiredMode = REQUIRED)
        List<ScorePercent> starPercent,
        @Schema(requiredMode = REQUIRED)
        Boolean isLiked,
        @Schema(requiredMode = REQUIRED)
        Long normalPrice,
        @Schema(requiredMode = REQUIRED)
        String productDetail,
        @Schema(requiredMode = REQUIRED)
        String ingredients,
        @Schema(requiredMode = REQUIRED)
        String oliveYoungUrl,
        @Schema(requiredMode = REQUIRED)
        String q10Url,
        @Schema(requiredMode = REQUIRED)
        MiddleCategory middleCategory,
        @Schema(requiredMode = REQUIRED)
        SubCategory subCategory
) {
    public static ProductDetailResponse from(ProductResponse response, List<ProductOptionResponse> productOptions,
                                             Product product, List<ScorePercent> starPercent, Boolean isLiked) {

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
                product.getOliveYoungUrl(),
                product.getQoo10Url(),
                product.getMiddleCategory(),
                product.getSubCategory()
        );
    }
}
