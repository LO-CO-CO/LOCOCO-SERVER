package com.lokoko.domain.creator.api.dto.request;

import com.lokoko.domain.creator.domain.entity.enums.ContentLanguage;
import com.lokoko.domain.creator.domain.entity.enums.Gender;
import com.lokoko.domain.creator.domain.entity.enums.SkinTone;
import com.lokoko.domain.creator.domain.entity.enums.SkinType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatorInfoUpdateRequest(
        @NotBlank(message = "크리에이터 ID는 필수입니다")
        @Size(min = 1, max = 15, message = "크리에이터 ID는 1자 이상 15자 이하여야 합니다")
        @Pattern(regexp = "^[a-z0-9._]+$",
                message = "크리에이터 ID는 영문(소문자), 숫자, 점(.), 언더바(_)만 사용 가능합니다")
        String creatorName,

        String birthDate,
        Gender gender,

        @NotBlank(message = "이름은 필수입니다")
        String firstName,

        @NotBlank(message = "성은 필수입니다")
        String lastName,

        @NotBlank(message = "국가 코드는 필수입니다")
        String countryCode,

        @NotBlank(message = "전화번호는 필수입니다")
        String phoneNumber,

        @NotNull(message = "언어 선택은 필수입니다")
        ContentLanguage contentLanguage,

        @NotBlank(message = "국가는 필수입니다")
        String country,

        @NotBlank(message = "주/도는 필수입니다")
        String stateOrProvince,

        @NotBlank(message = "도시는 필수입니다")
        String cityOrTown,

        @NotBlank(message = "주소는 필수입니다")
        String addressLine1,

        String addressLine2,
        String postalCode,

        @NotNull(message = "피부 타입은 필수입니다")
        SkinType skinType,

        @NotNull(message = "피부 톤은 필수입니다")
        SkinTone skinTone

) {
}