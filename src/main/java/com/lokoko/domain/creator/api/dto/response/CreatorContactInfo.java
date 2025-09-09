package com.lokoko.domain.creator.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreatorContactInfo(

        /**
         * 크리에이터 연락처 정보 (이메일, 국가번호, 전화번호)
         */

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Email", example = "huncozyboy@example.com")
        String email,

        @Schema(description = "국가번호", example = "+82")
        String countryCode,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "전화번호 (최대 15자)", example = "010123456789")
        String phoneNumber
) {
}
