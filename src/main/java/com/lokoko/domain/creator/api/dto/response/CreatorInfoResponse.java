package com.lokoko.domain.creator.api.dto.response;

import com.lokoko.domain.creator.domain.entity.enums.ContentLanguage;
import com.lokoko.domain.creator.domain.entity.enums.Gender;
import com.lokoko.domain.creator.domain.entity.enums.SkinTone;
import com.lokoko.domain.creator.domain.entity.enums.SkinType;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


public record CreatorInfoResponse(
        @Schema(requiredMode = REQUIRED, description = "크리에이터 이름", example = "huncozyboy")
        String creatorName,

        @Schema(description = "생년월일", example = "1995-03-15")
        String birthDate,

        @Schema(description = "성별", example = "FEMALE")
        Gender gender,

        @Schema(requiredMode = REQUIRED, description = "이름", example = "Jessica")
        String firstName,

        @Schema(requiredMode = REQUIRED, description = "성", example = "Anderson")
        String lastName,

        @Schema(requiredMode = REQUIRED, description = "국가번호", example = "+82")
        String countryCode,

        @Schema(requiredMode = REQUIRED, description = "전화번호 (최대 15자)", example = "010123456789")
        String phoneNumber,

        @Schema(requiredMode = REQUIRED, description = "콘텐츠 언어", example = "ENGLISH")
        ContentLanguage contentLanguage,

        @Schema(requiredMode = REQUIRED, description = "국가(드롭다운 선택)", example = "US")
        String country,

        @Schema(requiredMode = REQUIRED, description = "주/도/광역시 (최대 20자)", example = "CA")
        String stateOrProvince,

        @Schema(requiredMode = REQUIRED, description = "City / Town (최대 20자)", example = "San Francisco")
        String cityOrTown,

        @Schema(requiredMode = REQUIRED, description = "Address Line 1 (텍스트, 최대 100자)", example = "1234 Market St")
        String addressLine1,

        @Schema(requiredMode = REQUIRED, description = "Address Line 2 (텍스트, 최대 100자)", example = "Apt 5B")
        String addressLine2,

        @Schema(requiredMode = REQUIRED, description = "ZIP Code (최대 10자, 미국은 필수)", example = "94103")
        String postalCode,

        @Schema(requiredMode = REQUIRED, description = "피부 타입 (드롭다운 6개)", example = "COMBINATION")
        SkinType skinType,

        @Schema(requiredMode = REQUIRED, description = "피부톤 (드롭다운 20개)", example = "SHADE_1")
        SkinTone skinTone
) {
}