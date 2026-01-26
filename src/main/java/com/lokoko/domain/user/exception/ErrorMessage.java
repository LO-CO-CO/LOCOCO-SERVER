package com.lokoko.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    USER_NOT_FOUND("존재하지 않는 유저입니다"),
    ADMIN_NOT_FOUND("존재하지 않는 Admin입니다"),
    ADMIN_CAMPAIGN_APPROVAL_NOT_ALLOWED("캠페인 승인 상태 변경이 가능한 상태가 아닙니다.");

    private final String message;
}
