package com.lokoko.domain.productBrand.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
public record ProductBrandNameListResponse(

        @Schema(requiredMode = REQUIRED, description = "상품 브랜드 이름 리스트")
        List<ProductBrandName> brandNames

) {
}
