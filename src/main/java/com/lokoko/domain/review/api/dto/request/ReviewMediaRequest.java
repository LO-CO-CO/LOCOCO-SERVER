package com.lokoko.domain.review.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReviewMediaRequest(
        @NotNull List<String> mediaType
) {
}
