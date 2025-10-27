package com.lokoko.domain.creator.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
@Schema(description = "크리에이터 정보")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreatorInfo(
        @Schema(requiredMode = REQUIRED, description = "크리에이터 ID", example = "10")
        Long creatorId,

        @Schema(requiredMode = REQUIRED, description = "크리에이터 풀 네임", example = "김지수")
        String creatorFullName,

        @Schema(requiredMode = REQUIRED, description = "크리에이터 닉네임", example = "jisoo_creator")
        String creatorNickname,

        @Schema(requiredMode = NOT_REQUIRED, description = "프로필 이미지 URL", example = "https://s3.example.com/profile/creator-10.jpg")
        String profileImageUrl,

        @Schema(description = "인스타그램 계정 링크", example = "https://www.instagram.com/_hyon.8x21?igsh=MWU4cTI5aw==")
        String instaLink,

        @Schema(description = "틱톡 계정 링크", example = "https://www.tiktok.com/@hyoeun")
        String tiktokLink
) {
}
