package com.lokoko.domain.media.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
public record ProductImageResponse(

        @Schema(requiredMode = REQUIRED, description = "presignedUrl 리스트", example = "https://s3.ap-northeast-2.amazonaws.com/...")
        List<String> mediaUrl
) {
}