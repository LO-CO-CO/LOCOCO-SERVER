package com.lokoko.domain.user.api.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
public record AdminProductCreateResponse(

        @Schema(requiredMode = REQUIRED, description = "생성된 상품 id")
        Long productId
) {
}