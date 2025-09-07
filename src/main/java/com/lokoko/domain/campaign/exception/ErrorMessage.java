package com.lokoko.domain.campaign.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    CAMPAIGN_NOT_FOUND("존재하지 않는 캠페인입니다."),
    INVALID_CAMPAIGN_STATUS("해당 캠페인은 조회 가능한 상태가 아닙니다. (미승인 / 작성 상태)");

    private final String message;
}
