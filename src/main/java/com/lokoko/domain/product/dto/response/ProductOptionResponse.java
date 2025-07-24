package com.lokoko.domain.product.dto.response;

import com.lokoko.domain.product.entity.ProductOption;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

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
