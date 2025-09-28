package com.lokoko.domain.creator.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.creator.domain.entity.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreatorBasicInfo(

        /**
         * 크리에이터 기본 정보 (크리에이터 ID, 프로필 이미지 URL, 크리에이터명, 이름, 성)
         */
        @Schema(requiredMode = REQUIRED, description = "크리에이터 ID", example = "123")
        Long creatorId,

        @Schema(requiredMode = REQUIRED, description = "프로필 이미지 URL", example = "https://s3.example.com/profile/us-user-1001.jpg")
        String profileImageUrl,

        @Schema(requiredMode = REQUIRED, description = "크리에이터 이름", example = "huncozyboy")
        String creatorName,

        @Schema(requiredMode = REQUIRED, description = "이름", example = "Jessica")
        String firstName,

        @Schema(requiredMode = REQUIRED, description = "성", example = "Anderson")
        String lastName,

        @Schema(requiredMode = REQUIRED, description = "성별", example = "FEMALE")
        Gender gender,

        @Schema(requiredMode = REQUIRED, description = "생년월일(YYYY-MM-DD)", example = "1999-10-19")
        String birthDate,

        @Schema(requiredMode = REQUIRED, description = "이메일", example = "chanel@gmail.com")
        String email,

        @Schema(requiredMode = REQUIRED, description = "크리에이터 레벨", example = "PRO / NORMAL")
        String creatorLevel

) {
}
