package com.lokoko.domain.campaign.api.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    CAMPAIGN_DETAIL_GET_SUCCESS("캠페인 상세조회에 성공했습니다");

    private final String message;
}
