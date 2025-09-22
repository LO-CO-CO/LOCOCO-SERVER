package com.lokoko.domain.creator.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreatorProfileImageRequest(
        
        @NotNull
        String mediaType
) {
}
