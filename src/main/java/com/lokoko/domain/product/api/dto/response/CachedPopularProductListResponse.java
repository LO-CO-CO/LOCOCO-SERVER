package com.lokoko.domain.product.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record CachedPopularProductListResponse(
        @Schema(requiredMode = REQUIRED)
        String searchQuery,

        @Schema(requiredMode = REQUIRED)
        List<CachedPopularProduct> products,

        @Schema(requiredMode = REQUIRED)
        PageableResponse pageInfo
) {
}
