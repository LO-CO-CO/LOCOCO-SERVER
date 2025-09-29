package com.lokoko.domain.customer.api.dto.request;

import com.lokoko.domain.creator.domain.entity.enums.ContentLanguage;
import com.lokoko.domain.creator.domain.entity.enums.Gender;
import com.lokoko.domain.creator.domain.entity.enums.SkinTone;
import com.lokoko.domain.creator.domain.entity.enums.SkinType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CustomerMyPageRequest(

        @Schema(description = "Customer 프로필 이미지 url", example = "https://s3.example.com/profile/us-user-1001.jpg")
        String profileImageUrl,

        @Size(min = 1, max = 15, message = "ID는 1자 이상 15자 이하여야 합니다")
        @Pattern(regexp = "^[a-z0-9._]+$",
                message = "ID는 영문(소문자), 숫자, 점(.), 언더바(_)만 사용 가능합니다")
        String customerName,

        @Schema(description = "생년월일", example = "2002-08-21")
        LocalDate birthDate,

        @Schema(description = "성별", example = "MALE")
        Gender gender,

        @NotBlank(message = "이름은 필수입니다")
        @Schema(description = "이름", example = "Jessica")
        String firstName,

        @NotBlank(message = "성은 필수입니다")
        @Schema(description = "성", example = "Anderson")
        String lastName,

        @Schema(description = "국가번호 (선택, 최대 5자)", example = "+1")
        @Size(max = 5)
        String countryCode,

        @Schema(description = "전화번호 (선택, 최대 20자)", example = "01012345678")
        @Size(max = 20)
        String phoneNumber,

        @Schema(description = "콘텐츠 언어", example = "ENGLISH")
        ContentLanguage contentLanguage,

        @Schema(description = "구글 로그인시 받은 email", example = "lococo@example.com")
        String email,

        @Schema(description = "국가", example = "US")
        String country,

        @Schema(description = "State (텍스트 최대 20자)", example = "CA")
        @Size(max = 20)
        String stateOrProvince,

        @Schema(description = "City/Town (텍스트, 최대 20자)", example = "San Francisco")
        @Size(max = 20)
        String cityOrTown,

        @Schema(description = "Address Line 1 (최대 200자)", example = "1234 Market St")
        @Size(max = 200)
        String addressLine1,

        @Schema(description = "Address Line 2 (최대 200자)", example = "Apt 5B")
        @Size(max = 200)
        String addressLine2,

        @Schema(description = "ZIP Code (최대 10자)", example = "94103")
        @Size(max = 10)
        String postalCode,

        @Schema(description = "피부 타입 (드롭다운 6개)", example = "COMBINATION")
        SkinType skinType,

        @Schema(description = "피부 톤 (드롭다운 20개)", example = "SHADE_12")
        SkinTone skinTone

) {
}