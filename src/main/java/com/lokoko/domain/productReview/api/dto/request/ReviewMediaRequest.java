package com.lokoko.domain.productReview.api.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ReviewMediaRequest(
        @NotNull List<String> mediaType
) {
}
