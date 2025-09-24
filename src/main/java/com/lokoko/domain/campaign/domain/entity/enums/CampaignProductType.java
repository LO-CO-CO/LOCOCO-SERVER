package com.lokoko.domain.campaign.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CampaignProductType {

    SKINCARE("Skincare"),
    SUNCARE("Suncare"),
    MAKEUP("Makeup");

    private final String displayName;
}
