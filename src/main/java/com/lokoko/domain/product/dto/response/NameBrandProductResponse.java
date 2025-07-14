package com.lokoko.domain.product.dto.response;

import com.lokoko.domain.product.dto.ProductMainImageResponse;
import com.lokoko.global.common.response.PageableResponse;
import java.util.List;

public record NameBrandProductResponse(
        String searchQuery,
        List<ProductMainImageResponse> products,
        PageableResponse pageInfo
) {
}
