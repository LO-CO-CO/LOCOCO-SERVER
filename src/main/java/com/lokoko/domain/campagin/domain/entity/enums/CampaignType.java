package com.lokoko.domain.campagin.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 캠페인 종류에 대한 enum 입니다.
 */
@Getter
@RequiredArgsConstructor
public enum CampaignType {
    
    GIVEAWAY("Giveaway Campaign", false, false),
    CONTENTS("Contents Campaign", true, true),
    EXCLUSIVE("Exclusive Campaign", true, true);
    
    private final String displayName;
    private final boolean isPaid;
    private final boolean isVipOnly;

}
