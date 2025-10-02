package com.lokoko.domain.scheduler.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 스케줄링 이벤트 상태
 */
@Getter
@RequiredArgsConstructor
public enum EventStatus {

    PENDING("대기중"),
    PROCESSING("처리중"),
    EXECUTED("실행완료"),
    FAILED("실패");

    private final String description;
}