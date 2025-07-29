package com.lokoko.domain.product.api.dto.request;

import com.lokoko.domain.product.domain.entity.enums.MainCategory;
import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;

public record CrawlRequest(
        MainCategory mainCategory,
        MiddleCategory middleCategory
) {
}
