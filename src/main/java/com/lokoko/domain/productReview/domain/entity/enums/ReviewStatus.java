package com.lokoko.domain.productReview.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewStatus {
    SUBMITTED, // 크리에이터가 최초 제출함
    REVISION_REQUESTED, // 브랜드가 수정 요청함
    RESUBMITTED, // 크리에이터가 수정 후 재제출함
}
