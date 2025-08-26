package com.lokoko.domain.product.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.lokoko.domain.product.domain.entity.ProductOption;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductOptionResponse(
        @Schema(requiredMode = REQUIRED)
        Long id,
        @Schema(requiredMode = REQUIRED)
        String optionName
) {
    public static ProductOptionResponse from(ProductOption option) {
        return new ProductOptionResponse(option.getId(), option.getOptionName());
    }
}
