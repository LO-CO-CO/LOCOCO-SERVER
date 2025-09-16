package com.lokoko.domain.campaign.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CampaignLanguage {

    ENG("Eng"),
    ESN("Esn");

    private final String displayName;
}
