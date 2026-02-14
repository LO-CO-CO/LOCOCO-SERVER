package com.lokoko.domain.customer.api.dto.request;

import com.lokoko.domain.creator.domain.entity.enums.Gender;
import com.lokoko.domain.creator.domain.entity.enums.SkinTone;
import com.lokoko.domain.creator.domain.entity.enums.SkinType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerInfoRegisterRequest(

        @NotBlank(message = "커뮤니티 ID는 필수입니다")
        @Size(min = 1, max = 15, message = "커뮤니티 ID는 1자 이상 15자 이하여야 합니다")
        @Pattern(regexp = "^[a-z0-9._]+$",
                message = "커뮤니티 ID는 영문(소문자), 숫자, 점(.), 언더바(_)만 사용 가능합니다")
        String communityName,

        @NotBlank(message = "생년월일은 필수입니다")
        @Schema(description = "생년월일", example = "2002-08-21")
        String birthDate,

        @NotNull(message = "성별은 필수입니다")
        @Schema(description = "성별", example = "MALE")
        Gender gender,

        @NotBlank(message = "이름은 필수입니다")
        @Schema(description = "이름", example = "Jessica")
        String firstName,

        @NotBlank(message = "성은 필수입니다")
        @Schema(description = "성", example = "Anderson")
        String lastName,

        @Schema(description = "국가", example = "US")
        String country,

        @NotBlank(message = "국가번호는 필수입니다")
        @Schema(description = "국가번호 (최대 5자)", example = "+1")
        @Size(max = 5)
        String countryCode,

        @NotBlank(message = "전화번호는 필수입니다")
        @Schema(description = "전화번호 (최대 20자)", example = "01012345678")
        @Size(max = 20)
        String phoneNumber,

        @NotNull(message = "피부 타입은 필수입니다")
        @Schema(description = "피부 타입 (드롭다운 6개)", example = "COMBINATION")
        SkinType skinType,

        @NotNull(message = "피부 톤은 필수입니다")
        @Schema(description = "피부 톤 (드롭다운 20개)", example = "SHADE_12")
        SkinTone skinTone
) {
}
