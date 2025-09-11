package com.lokoko.domain.campaign.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record CampaignMediaResponse(
        @Schema(requiredMode = REQUIRED)
        List<String> mediaUrl
) {
}
