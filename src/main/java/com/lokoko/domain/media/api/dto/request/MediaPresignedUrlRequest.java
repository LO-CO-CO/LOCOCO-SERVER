package com.lokoko.domain.media.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record MediaPresignedUrlRequest(

        @NotEmpty
        List<@NotBlank String> mediaType
) {
}
