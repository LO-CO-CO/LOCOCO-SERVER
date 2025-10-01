package com.lokoko.domain.campaignReview.domain.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContentStatus {
    NOT_SUBMITTED("미제출"),
    IN_PROGRESS("진행중"),
    PENDING_REVISION("검토 요청"),
    REVISING("수정중"),
    FINAL_UPLOADED("최종 업로드"),
    UNKNOWN("알 수 없음");

    private final String description;
}
