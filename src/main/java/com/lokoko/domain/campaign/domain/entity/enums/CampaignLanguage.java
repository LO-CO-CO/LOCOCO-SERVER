package com.lokoko.domain.campaign.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CampaignLanguage {

    EN("EN"),
    ES("ESN");

    private final String displayName;
}
