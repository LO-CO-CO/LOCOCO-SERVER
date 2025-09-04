package com.lokoko.domain.campaign.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    CAMPAIGN_NOT_FOUND("존재하지 않는 캠페인입니다.");

    private final String message;
}
