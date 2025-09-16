package com.lokoko.domain.brand.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record BrandProfileImageRequest(
        @NotNull String mediaType) {
}
