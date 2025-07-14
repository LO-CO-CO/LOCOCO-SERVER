package com.lokoko.domain.product.dto.response;

import com.lokoko.domain.product.dto.ProductMainImageResponse;
import com.lokoko.global.common.response.PageableResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record CategoryProductPageResponse(
        String searchQuery,
        String parentCategoryName,
        List<ProductMainImageResponse> products,
        PageableResponse pageInfo
) {
}
