package com.lokoko.domain.creator.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.creator.domain.entity.enums.CreatorStatus;
import com.lokoko.domain.creator.domain.entity.enums.CreatorType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreatorMyPageResponse(

        /**
         * 크리에이터 마이페이지 응답 (ID, 기본 정보, 연락처 정보, 주소 정보, 피부 정보, 크리에이터 유형, 크리에이터 상태)
         */
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "크리에이터 ID", example = "3")
        Long creatorId,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "기본 정보")
        CreatorBasicInfo creatorBasicInfo,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "연락처 정보")
        CreatorContactInfo creatorContactInfo,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "주소 정보")
        CreatorAddressInfo creatorAddressInfo,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "피부 정보")
        CreatorFaceInfo creatorFaceInfo,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "크리에이터 타입", example = "VIP")
        CreatorType creatorType,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "크리에이터 승인 상태", example = "NOT_APPROVED")
        CreatorStatus creatorStatus
) {
}
