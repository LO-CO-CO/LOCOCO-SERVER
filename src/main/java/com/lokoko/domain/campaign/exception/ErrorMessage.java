package com.lokoko.domain.campaign.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    CAMPAIGN_NOT_FOUND("존재하지 않는 캠페인입니다."),
    CAMPAIGN_EXPIRED("이미 만료된 캠페인입니다."),
    CAMPAIGN_NOT_BELONG_TO_CREATOR("본인이 참여하고 있는 캠페인이 아닙니다.");

    private final String message;
}
