package com.lokoko.domain.product.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record NewProductsByCategoryResponse(
        @Schema(requiredMode = REQUIRED)
        String searchQuery,

        @Schema(requiredMode = REQUIRED)
        List<ProductBasicResponse> products,

        @Schema(requiredMode = REQUIRED)
        PageableResponse pageInfo
) {
}
