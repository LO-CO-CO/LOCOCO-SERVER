package com.lokoko.domain.scheduler.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 스케줄링 이벤트 타입
 */
@Getter
@RequiredArgsConstructor
public enum EventType {

    // 캠페인 상태 변경 이벤트
    CAMPAIGN_START_RECRUITING("캠페인 모집 시작"),
    CAMPAIGN_CLOSE_RECRUITMENT("캠페인 모집 마감"),
    CAMPAIGN_START_REVIEW("캠페인 리뷰 시작"),
    CAMPAIGN_COMPLETE("캠페인 완료"),

    // 크리에이터 관련 이벤트
    CREATOR_ANNOUNCEMENT_PROCESS("크리에이터 발표 처리"),
    CREATOR_FIRST_REVIEW_DEADLINE("1차 리뷰 마감 처리"),
    CREATOR_SECOND_REVIEW_DEADLINE("2차 리뷰 마감 처리"),
    CREATOR_ADDRESS_DEADLINE("배송지 입력 마감 처리");

    private final String description;
}