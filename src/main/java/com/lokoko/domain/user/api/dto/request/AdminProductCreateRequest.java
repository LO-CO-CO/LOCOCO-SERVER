package com.lokoko.domain.user.api.dto.request;

import com.lokoko.domain.media.api.dto.request.ProductImageRequest;
import com.lokoko.domain.product.domain.entity.enums.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record AdminProductCreateRequest(

        @Schema(requiredMode = REQUIRED, description = "상품명 (최대 30자)", example = "비타C 브라이트닝 세럼")
        @NotBlank
        @Size(max = 30)
        String productName,

        @Schema(requiredMode = REQUIRED, description = "상품 브랜드 id", example = "1")
        @NotNull
        Long productBrandId,

        @Schema(requiredMode = REQUIRED, description = "가격", example = "19900")
        @NotNull
        @PositiveOrZero
        Long normalPrice,

        @Schema(description = "용량 (최대 20자)", example = "30ml")
        @NotNull
        @Size(max = 20)
        String unit,

        @Schema(requiredMode = REQUIRED, description = "카테고리", example = "SERUM_AMPOULE")
        @NotNull
        ProductCategory category,

        @Schema(requiredMode = REQUIRED, description = "제품 제조 날짜")
        @NotNull
        Instant manufacturedAt,

        @Schema(description = "상품 상세 설명 (최대 5000자)")
        @Size(max = 5000)
        String productDetail,

        @Schema(description = "상품 성분 (최대 5000자)")
        @Size(max = 5000)
        String ingredients,

        @Schema(requiredMode = REQUIRED, description = "상품 이미지 목록 (최소 1개, 최대 5개)")
        @NotEmpty(message = "상품 이미지는 최소 1개 이상 필요합니다")
        @Size(max = 5, message = "상품 이미지는 최대 5개까지 가능합니다")
        List<ProductImageRequest> images
) {
}