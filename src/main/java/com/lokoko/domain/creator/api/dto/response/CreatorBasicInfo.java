package com.lokoko.domain.creator.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreatorBasicInfo(

        /**
         * 크리에이터 기본 정보 (프로필 이미지 URL, 크리에이터명, 이름, 성)
         */
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "프로필 이미지 URL", example = "https://s3.example.com/profile/us-user-1001.jpg")
        String profileImageUrl,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "크리에이터 이름", example = "huncozyboy")
        String creatorName,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "이름", example = "Jessica")
        String firstName,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "성", example = "Anderson")
        String lastName
) {
}
