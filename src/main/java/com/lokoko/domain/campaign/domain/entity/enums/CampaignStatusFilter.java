package com.lokoko.domain.campaign.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CampaignStatusFilter {
    ALL("모두"),
    DRAFT("임시 저장"),
    WAITING_APPROVAL("대기 중"),
    OPEN_RESERVED("오픈 예정"),
    ACTIVE("진행 중"),
    COMPLETED("종료");

    private final String displayName;
}
