package com.lokoko.domain.campaign.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CampaignMediaRequest(
        @NotNull List<String> mediaType
) {
}
