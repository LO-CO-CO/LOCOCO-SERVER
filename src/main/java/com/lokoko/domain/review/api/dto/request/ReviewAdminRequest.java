package com.lokoko.domain.review.api.dto.request;

import com.lokoko.global.common.entity.MediaType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ReviewAdminRequest(
        Long productOptionId,
        @NotNull Integer rating,
        @NotNull @Size(min = 15, max = 1500) String positiveComment,
        @NotNull @Size(min = 15, max = 1500) String negativeComment,
        MediaType mediaType,
        String videoUrl,           // mediaType == VIDEO 일 때 사용
        @Size(min = 1) List<String> imageUrl, // mediaType == IMAGE 일 때 사용
        String receiptUrl
) {
}
