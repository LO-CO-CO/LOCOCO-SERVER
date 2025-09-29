package com.lokoko.domain.campaign.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CampaignDetailPageStatus {
    // 지원 없는 상태
    OPEN_RESERVED("OPEN_RESERVED", "Coming Soon"),
    RECRUITING("RECRUITING", "Apply Now!"),
    NOT_APPLIED_ENDED("NOT_APPLIED_ENDED", "Closed"),

    // 지원 있는 상태 - 대기/거절
    APPLIED("APPLIED", "Successfully Applied"),
    REJECTED("REJECTED", "Campaign not Selected"),

    // 승인됨 - 진행 상태들
    APPROVED_SECOND_REVIEW_DONE("APPROVED_SECOND_REVIEW_DONE", "Completed"),

    // 만료 상태들
    APPROVED_ADDRESS_NOT_CONFIRMED("APPROVED_ADDRESS_NOT_CONFIRMED", "Expired"),
    APPROVED_REVIEW_NOT_CONFIRMED("APPROVED_REVIEW_NOT_CONFIRMED", "Expired"),

    // 비로그인 유저 , customer , brand 가 확인할 수 있는 상태
    ACTIVE("ACTIVE", "Campaign in progress"),
    CLOSED("CLOSED", "Closed");

    private final String code;
    private final String message;
}
