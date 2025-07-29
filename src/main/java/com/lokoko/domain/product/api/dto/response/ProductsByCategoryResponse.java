package com.lokoko.domain.product.api.dto.response;

import com.lokoko.global.common.response.PageableResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record ProductsByCategoryResponse(
        String searchQuery,
        String parentCategoryName,
        List<ProductListItemResponse> products,
        PageableResponse pageInfo
) {
}
