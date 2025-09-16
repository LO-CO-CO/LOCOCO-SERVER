package com.lokoko.domain.campaign.api.dto.response;

import com.lokoko.domain.image.domain.entity.CampaignImage;

public record CampaignImageResponse(
        Long id,
        String url,
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
