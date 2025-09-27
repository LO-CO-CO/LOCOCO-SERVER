package com.lokoko.domain.creatorCampaign.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    ALREADY_PARTICIPATED_CAMPAIGN("이미 참여한 캠페인입니다."),
    CAMPAIGN_NOT_STARTED("아직 시작하지 않은 캠페인입니다."),
    CAMPAIGN_RECRUITMENT_FULL("캠페인 모집 인원이 모두 찼습니다."),
    CAMPAIGN_NOT_RECRUITING("현재 모집 중이 아닌 캠페인입니다."),
    CREATOR_CAMPAIGN_REVIEW_ABLE_NOT_FOUND("리뷰 업로드 가능한 캠페인 참여 이력이 없습니다.");

    private final String message;
}
