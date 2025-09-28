package com.lokoko.domain.creator.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lokoko.global.common.response.PageableResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreatorMyCampaignListResponse(

        @Schema(requiredMode = REQUIRED, description = "크리에이터 기본 정보")
        CreatorBasicInfo basicInfo,

        @Schema(requiredMode = REQUIRED)
        List<CreatorMyCampaignResponse> campaigns,

        @Schema(requiredMode = REQUIRED)
        PageableResponse pageInfo
) {
}
