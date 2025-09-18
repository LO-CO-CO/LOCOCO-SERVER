package com.lokoko.domain.customer.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record CustomerProfileImageRequest(
        @NotNull String mediaType) {
}
