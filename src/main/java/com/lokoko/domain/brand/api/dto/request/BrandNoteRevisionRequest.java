package com.lokoko.domain.brand.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record BrandNoteRevisionRequest(

        @NotBlank(message = "브랜드 노트는 비어 있을 수 없습니다.")
        String brandNote
) {
}
