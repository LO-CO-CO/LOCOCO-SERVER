package com.lokoko.domain.media.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProductImagePresignedUrlRequest(

        @NotEmpty(message = "mediaType은 최소 1개 이상 필요합니다")
        @Size(min = 1, max = 5, message = "mediaType은 최대 5개까지 가능합니다")
        List<@NotBlank(message = "mediaType은 비어있을 수 없습니다") String> mediaType

) {
}