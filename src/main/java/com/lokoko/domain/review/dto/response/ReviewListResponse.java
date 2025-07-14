package com.lokoko.domain.review.dto.response;

import com.lokoko.global.common.response.PageableResponse;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.data.domain.Slice;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
public record ReviewListResponse<T>(
        @Schema(requiredMode = REQUIRED)
        String searchQuery,
        @Schema(requiredMode = REQUIRED)
        List<T> reviews,
        @Schema(requiredMode = REQUIRED)
        PageableResponse pageInfo

) {
    public static <T> ReviewListResponse<T> from(String keyword, Slice<T> reviews) {
        return new ReviewListResponse(
                keyword,
                reviews.getContent(),
                PageableResponse.of(reviews)
        );
    }
}
