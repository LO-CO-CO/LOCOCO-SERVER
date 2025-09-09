package com.lokoko.domain.productReview.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ReviewRequest(
        Long productOptionId,
        @NotNull Integer rating,
        @NotNull @Size(min = 15, max = 1500) String positiveComment,
        @NotNull @Size(min = 15, max = 1500) String negativeComment,
        List<String> mediaUrl,
        List<String> receiptUrl
) {
}
