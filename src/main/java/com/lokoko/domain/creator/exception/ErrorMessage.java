package com.lokoko.domain.creator.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    CREATOR_ID_ALREADY_EXIST("이미 존재하는 크리에이터 ID입니다."),
    CREATOR_NOT_FOUND("이미 존재하는 크리에이터 ID입니다."),
    SNS_NOT_CONNECTED("SNS는 필수적으로 1개 이상 연결되어야 합니다."),
    CREATOR_BASIC_INFO_NOT_COMPLETED("크리에이터 필수 정보 입력이 아직 완료되지 않은 상태입니다.");

    private final String message;
}
