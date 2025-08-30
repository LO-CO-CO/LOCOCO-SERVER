package com.lokoko.domain.campagin.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 크리에이터가 캠페인에 지원한 상황에서, 세부 상태에 대한 정보를 나타내는 enum
 */
@Getter
@RequiredArgsConstructor
public enum ParticipationStatus {

    //대기
    PENDING("Pending"), // 캠페인 지원 후 결과 대기 중

    // 승인/거절
    APPROVED("Approved"), // 캠페인 당첨
    REJECTED("Rejected"), // 캠페인 당첨 X

    // 진행
    /**
     * 캠페인 당첨 O , 배송지 확인 후
     * 캠페인 당첨 O , 1차 리뷰 업로드 완료
     * 캠페인 당첨 O , 브랜드 수정사항 남김
     * 캠페인 당첨 O , 브랜드 수정사항 확인 후
     */
    ACTIVE("Active"),

    // 완료
    COMPLETED("Completed"), // 캠페인 당첨 O , 2차 리뷰 업로드 완료

    // 만료
    EXPIRED("Expired"); // 캠페인 당첨 O , 배송지 확인 X  || 캠페인 당첨 O || 리뷰 업로드 X


    private final String displayName;

}
