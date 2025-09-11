package com.lokoko.domain.productReview.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record ReviewReceiptRequest(
        @NotNull String mediaType
) {
}
