package com.lokoko.domain.product.dto.response;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record CategoryPopularProductResponse(
        @Schema(requiredMode = REQUIRED)
        String searchQuery,

        @Schema(requiredMode = REQUIRED)
        List<ProductResponse> products,

        @Schema(requiredMode = REQUIRED)
        PageableResponse pageInfo
) {
}
