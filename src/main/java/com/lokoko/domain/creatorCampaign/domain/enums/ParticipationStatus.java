package com.lokoko.domain.creatorCampaign.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 크리에이터가 캠페인에 지원한 상황에서, 세부 상태에 대한 정보를 나타내는 enum
 */
@Getter
@RequiredArgsConstructor
public enum ParticipationStatus {

    /**
     * 대기 (PENDING) 캠페인 지원 후 결과 대기 중
     */
    PENDING,

    /**
     * 승인/거절 (APPROVED / REJECTED)
     */
    APPROVED, // 캠페인 당첨
    REJECTED, // 캠페인 당첨 X

    /**
     * 진행 (ACTIVE) 캠페인 당첨 O , 배송지 입력 완료한 상태 캠페인 당첨 O , 1차 리뷰 업로드 완료한 상태 캠페인 당첨 O , 브랜드가 수정사항 남긴 상태 캠페인 당첨 O , 크리에이터가 브랜드
     * 수정사항 확인한 후의 상태
     */
    APPROVED_ADDRESS_CONFIRMED,
    APPROVED_FIRST_REVIEW_DONE,
    APPROVED_REVISION_REQUESTED,
    APPROVED_REVISION_CONFIRMED,

    /**
     * 완료 (COMPLETED) 캠페인 당첨 O, 2차 리뷰 업로드 완료
     */
    APPROVED_SECOND_REVIEW_DONE,

    /**
     * 만료 (EXPIRED) 캠페인 당첨 O , 배송지 입력 X 캠페인 당첨 O || 리뷰 업로드 X
     */
    APPROVED_ADDRESS_NOT_CONFIRMED,
    APPROVED_REVIEW_NOT_CONFIRMED

}
