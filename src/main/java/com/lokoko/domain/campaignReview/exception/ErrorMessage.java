package com.lokoko.domain.campaignReview.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    // CampaignReview 관련
    REVIEW_ALREADY_SUBMITTED("이미 해당 단계 리뷰를 제출했습니다");

    private final String message;
}
