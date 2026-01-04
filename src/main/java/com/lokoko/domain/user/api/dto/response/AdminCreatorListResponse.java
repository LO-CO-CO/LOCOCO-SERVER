package com.lokoko.domain.user.api.dto.response;

import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record AdminCreatorListResponse(
        @Schema(requiredMode = REQUIRED, description = "전체 크리에이터 목록")
        List<AdminCreator> creators,
        @Schema(requiredMode = REQUIRED, description = "총 크리에이터 수")
        Long totalCreatorCount,
        @Schema(requiredMode = REQUIRED, description = "페이징 정보")
        PageableResponse pageInfo
) {
}
