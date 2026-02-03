package com.lokoko.domain.product.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record NewProductsByCategoryResponse(
        @Schema(requiredMode = REQUIRED)
        List<SimpleProductResponse> products
) {
}
