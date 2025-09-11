package com.lokoko.domain.creator.api.dto.response;

import com.lokoko.domain.creator.domain.entity.enums.ContentLanguage;
import com.lokoko.domain.creator.domain.entity.enums.Gender;
import com.lokoko.domain.creator.domain.entity.enums.SkinTone;
import com.lokoko.domain.creator.domain.entity.enums.SkinType;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


public record CreatorInfoResponse(
        @Schema(requiredMode = REQUIRED, description = "크리에이터 ID", example = "beauty_creator")
        String creatorName,

        @Schema(description = "생년월일", example = "1995-03-15")
        String birthDate,

        @Schema(description = "성별", example = "FEMALE")
        Gender gender,

        @Schema(requiredMode = REQUIRED, description = "이름", example = "Jane")
        String firstName,

        @Schema(requiredMode = REQUIRED, description = "성", example = "Doe")
        String lastName,

        @Schema(requiredMode = REQUIRED, description = "국가 코드", example = "+1")
        String countryCode,

        @Schema(requiredMode = REQUIRED, description = "전화번호", example = "555-123-4567")
        String phoneNumber,

        @Schema(requiredMode = REQUIRED, description = "콘텐츠 언어", example = "ENGLISH")
        ContentLanguage contentLanguage,

        @Schema(requiredMode = REQUIRED, description = "국가 (선택 시 필수, 그 외 국가 선택 시 선택)", example = "United States")
        String country,

        @Schema(requiredMode = REQUIRED, description = " 국가는 텍스트로 입력)", example = "California")
        String stateOrProvince,

        @Schema(requiredMode = REQUIRED, description = "도시/타운 (텍스트 필드로 입력)", example = "Los Angeles")
        String cityOrTown,

        @Schema(requiredMode = REQUIRED, description = "주소 1 (텍스트 필드로 입력)", example = "123 Main Street")
        String addressLine1,

        @Schema(description = "주소 2 (텍스트 필드로 입력)", example = "Apt 4B")
        String addressLine2,

        @Schema(description = "우편번호", example = "90001")
        String postalCode,

        @Schema(requiredMode = REQUIRED, description = "피부 타입 (드롭다운으로 총 6개 선택지 있음)", example = "OILY")
        SkinType skinType,

        @Schema(requiredMode = REQUIRED, description = "피부 톤 (드롭다운으로 총 20개 선택지 있음)", example = "MEDIUM")
        SkinTone skinTone
) {
}