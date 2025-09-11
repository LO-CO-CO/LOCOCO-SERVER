package com.lokoko.domain.creator.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.domain.creator.domain.entity.enums.SkinTone;
import com.lokoko.domain.creator.domain.entity.enums.SkinType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreatorFaceInfo(

        /**
         * 크리에이터 피부 정보 (피부타입, 피부톤)
         */

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "피부 타입 (드롭다운 6개)", example = "COMBINATION")
        SkinType skinType,

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "피부톤 (드롭다운 20개)", example = "SHADE_1")
        SkinTone skinTone
) {
}
