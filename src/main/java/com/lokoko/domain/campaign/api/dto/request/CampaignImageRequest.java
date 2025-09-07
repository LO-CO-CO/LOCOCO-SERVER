package com.lokoko.domain.campaign.api.dto.request;

import com.lokoko.domain.image.domain.entity.enums.ImageType;

public record CampaignImageRequest(
        String url,
        int displayOrder,
        ImageType imageType
) {
}
