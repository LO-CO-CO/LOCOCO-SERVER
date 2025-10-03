package com.lokoko.domain.scheduler.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 스케줄링 대상 타입
 */
@Getter
@RequiredArgsConstructor
public enum TargetType {

    CAMPAIGN("캠페인"),
    CREATOR_CAMPAIGN("크리에이터 캠페인");

    private final String description;
}