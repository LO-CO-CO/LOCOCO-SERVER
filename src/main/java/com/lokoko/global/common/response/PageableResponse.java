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
        Boolean isLast,
        @Schema(description = "전체 페이지 개수")
        Integer totalPages
) {
    public static PageableResponse of(Slice<?> slice) {
        return PageableResponse.builder()
                .pageNumber(slice.getNumber())
                .pageSize(slice.getSize())
                .numberOfElements(slice.getNumberOfElements())
                .isLast(slice.isLast())
                .totalPages(null)
                .build();
    }

    /**
     * 전체 페이지 정보와 함께 PageableResponse 생성
     */
    public static PageableResponse of(int pageNumber, int pageSize, int numberOfElements, boolean isLast, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        return PageableResponse.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .numberOfElements(numberOfElements)
                .isLast(isLast)
                .totalPages(totalPages)
                .build();
    }
}
