package com.lokoko.domain.creatorCampaign.api.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    PARTICIPATE_CAMPAIGN_SUCCESS("캠페인 참여 신청 성공");

    private final String message;
}
