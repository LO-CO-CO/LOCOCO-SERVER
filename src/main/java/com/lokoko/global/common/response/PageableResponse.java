package com.lokoko.global.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Slice;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
public record PageableResponse(
        @Schema(requiredMode = REQUIRED)
        Integer pageNumber,
        @Schema(requiredMode = REQUIRED)
        Integer pageSize,
        @Schema(requiredMode = REQUIRED)
        Integer numberOfElements,
        @Schema(requiredMode = REQUIRED)
        Boolean isLast
) {
    public static PageableResponse of(Slice<?> slice) {
        return PageableResponse.builder()
                .pageNumber(slice.getNumber())
                .pageSize(slice.getSize())
                .numberOfElements(slice.getNumberOfElements())
                .isLast(slice.isLast())
                .build();
    }
}
