package com.lokoko.domain.product.api.dto.response;

import com.lokoko.global.common.response.PageableResponse;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
public record ProductsByCategoryResponse(
        @Schema(requiredMode = REQUIRED)
        String searchQuery,
        @Schema(requiredMode = REQUIRED)
        String parentCategoryName,
        @Schema(requiredMode = REQUIRED)
        List<ProductListItemResponse> products,
        @Schema(requiredMode = REQUIRED)
        PageableResponse pageInfo
) {
}
