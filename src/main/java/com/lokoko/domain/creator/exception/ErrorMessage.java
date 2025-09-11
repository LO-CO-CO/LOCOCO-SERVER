package com.lokoko.domain.creator.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    CREATOR_ID_ALREADY_EXIST("이미 존재하는 크리에이터 ID입니다."),
    SNS_NOT_CONNECTED("SNS는 필수적으로 1개 이상 연결되어야 합니다."),
    CREATOR_BASIC_INFO_NOT_COMPLETED("크리에이터 필수 정보 입력이 아직 완료되지 않은 상태입니다."),
    ONLY_CREATOR_ROLE_SIGN_UP("오직 크리에이터만 가입할 수 있습니다"),

    // CampaignReview 관련
    CREATOR_NOT_FOUND("크리에이터가 존재하지 않습니다."),
    CREATOR_CAMPAIGN_NOT_FOUND("캠페인 참여 이력이 없습니다.");

    private final String message;
}
