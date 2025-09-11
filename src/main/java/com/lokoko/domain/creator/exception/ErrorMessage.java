package com.lokoko.domain.creator.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    // CampaignReview 관련
    CREATOR_NOT_FOUND("크리에이터가 존재하지 않습니다."),
    CREATOR_CAMPAIGN_NOT_FOUND("캠페인 참여 이력이 없습니다.");

    private final String message;
}
