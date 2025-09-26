package com.lokoko.domain.campaign.api.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.lokoko.domain.media.image.domain.entity.CampaignImage;
import io.swagger.v3.oas.annotations.media.Schema;

public record CampaignImageResponse(
        @Schema(requiredMode = REQUIRED, description = "이미지 id", example = "1")
        Long id,
        @Schema(requiredMode = REQUIRED, description = "이미지 url")
        String url,
        @Schema(requiredMode = REQUIRED, description = "이미지 표시 순서", example = "1")
        int displayOrder
) {
    public static CampaignImageResponse from(CampaignImage image) {
        return new CampaignImageResponse(
                image.getId(),
                image.getMediaFile().getFileUrl(),
                image.getDisplayOrder()
        );
    }
}
