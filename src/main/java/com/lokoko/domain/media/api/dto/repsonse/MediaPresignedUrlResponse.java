package com.lokoko.domain.media.api.dto.repsonse;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record MediaPresignedUrlResponse(

        @Schema(requiredMode = REQUIRED, description = "presignedUrl 리스트", example = "[\"https://s3.ap-northeast-2.amazonaws.com/...\", \"https://s3.ap-northeast-2.amazonaws.com/...\"]")
        List<String> mediaUrl
) {
}
