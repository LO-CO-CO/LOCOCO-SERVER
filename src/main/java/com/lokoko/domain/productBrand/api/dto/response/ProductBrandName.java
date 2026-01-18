package com.lokoko.domain.productBrand.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
public record ProductBrandName(

        @Schema(requiredMode = REQUIRED, description = "상품 브랜드 id")
        Long productBrandId,

        @Schema(requiredMode = REQUIRED, description = "상품 브랜드명")
        String productBrandName
) {
}
