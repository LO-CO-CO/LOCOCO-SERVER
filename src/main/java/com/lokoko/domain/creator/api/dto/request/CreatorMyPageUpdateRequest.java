package com.lokoko.domain.creator.api.dto.request;

import com.lokoko.domain.creator.domain.entity.enums.ContentLanguage;
import com.lokoko.domain.creator.domain.entity.enums.Gender;
import com.lokoko.domain.creator.domain.entity.enums.SkinTone;
import com.lokoko.domain.creator.domain.entity.enums.SkinType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreatorMyPageUpdateRequest(

        /**
         * 크리에이터 마이페이지 수정이 필요한 필드들만 입력받기 (입력받지 않은 필드들은 유지)
         */

        @Schema(description = "크리에이터 이름 (최대 30자, 영문/숫자/마침표/언더바만)", example = "hun_cozyboy.01")
        @Pattern(regexp = "^[a-z0-9._]+$")
        @Size(max = 15)
        String creatorName,

        @Schema(description = "이름", example = "Jessica")
        String firstName,

        @Schema(description = "성", example = "Anderson")
        String lastName,

        @Schema(description = "생년월일(YYYY-MM-DD)", example = "2001-10-19")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
        String birthDate,

        @Schema(description = "성별", example = "FEMALE")
        Gender gender,

        @Schema(description = "국가번호 (선택, 최대 5자)", example = "+1")
        @Size(max = 5)
        String countryCode,

        @Schema(description = "전화번호 (선택, 최대 20자)", example = "01012345678")
        @Size(max = 20)
        String phoneNumber,

        @Schema(description = "국가", example = "US")
        String country,

        @Schema(description = "State (텍스트 최대 20자)", example = "CA")
        @Size(max = 20)
        String stateOrProvince,

        @Schema(description = "City/Town (텍스트, 최대 20자)", example = "San Francisco")
        @Size(max = 20)
        String cityOrTown,

        @Schema(description = "Address Line 1 (최대 30자)", example = "1234 Market St")
        @Size(max = 30)
        String addressLine1,

        @Schema(description = "Address Line 2 (최대 30자)", example = "Apt 5B")
        @Size(max = 30)
        String addressLine2,

        @Schema(description = "ZIP Code (최대 10자)", example = "94103")
        @Size(max = 10)
        String postalCode,

        @Schema(description = "피부 타입 (드롭다운 6개)", example = "COMBINATION")
        SkinType skinType,

        @Schema(description = "피부 톤 (드롭다운 20개)", example = "SHADE_12")
        SkinTone skinTone,

        @Schema(description = "콘텐츠 언어", example = "ENGLISH")
        ContentLanguage contentLanguage
) {
}
