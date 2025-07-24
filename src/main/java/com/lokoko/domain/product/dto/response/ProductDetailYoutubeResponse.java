package com.lokoko.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record ProductDetailYoutubeResponse(
        @Schema(requiredMode = REQUIRED)
        List<String> youtubeUrls
) {
}
