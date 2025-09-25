package com.lokoko.domain.media.api.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MediaPresignedUrlRequest(
        
        @NotNull
        List<String> mediaType
) {
}
