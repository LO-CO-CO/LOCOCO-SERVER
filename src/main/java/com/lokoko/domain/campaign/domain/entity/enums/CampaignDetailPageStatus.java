package com.lokoko.domain.campaign.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CampaignDetailPageStatus {
    // 지원 없는 상태
    OPEN_RESERVED("Coming Soon"),
    RECRUITING("Apply Now!"),
    NOT_APPLIED_ENDED("Closed"),

    // 지원 있는 상태 - 대기/거절
    PENDING("Successfully Applied"),
    REJECTED("Campaign not Selected"),

    // 승인됨 - 진행 상태들
    APPROVED_SECOND_REVIEW_DONE("Completed"),

    // 만료 상태들
    APPROVED_ADDRESS_NOT_CONFIRMED("Expired"),
    APPROVED_REVIEW_NOT_CONFIRMED("Expired"),

    // 비로그인 유저 , customer , brand 가 확인할 수 있는 상태
    ACTIVE("Active"),
    CLOSED("Closed");


    private final String code;
}
