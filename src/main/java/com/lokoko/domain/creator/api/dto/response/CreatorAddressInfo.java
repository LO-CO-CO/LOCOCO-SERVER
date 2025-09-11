package com.lokoko.domain.creator.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreatorAddressInfo(

        /**
         * 크리에이터 주소 정보 (국가, 주/도, 도시/군/구, 상세주소1, 상세주소2, 우편번호)
         */
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "국가(드롭다운 선택)", example = "US")
        String country,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "주/도/광역시 (최대 20자)", example = "CA")
        String stateOrProvince,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "City / Town (최대 20자)", example = "San Francisco")
        String cityOrTown,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Address Line 1 (텍스트, 최대 30자)", example = "1234 Market St")
        String addressLine1,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Address Line 2 (텍스트, 최대 30자)", example = "Apt 5B")
        String addressLine2,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "ZIP Code (최대 10자, 미국은 필수)", example = "94103")
        String postalCode
) {
}
