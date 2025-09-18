package com.lokoko.domain.campaign.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CampaignChipStatus {

    DEFAULT("default"),
    DISABLED("disabled"),
    APPROVED("approved"),
    DECLINED("declined"),
    PROGRESS("progress");

    private final String displayName;
}
