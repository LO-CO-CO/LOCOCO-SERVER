package com.lokoko.domain.product.dto.response;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record NameBrandProductResponse(
        @Schema(requiredMode = REQUIRED)
        String searchQuery,

        @Schema(requiredMode = REQUIRED)
        List<ProductMainImageResponse> products,

        @Schema(requiredMode = REQUIRED)
        PageableResponse pageInfo
) {
}
