package com.lokoko.domain.campagin.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 브랜드 관점에서 보는 캠페인의 상태에 대한 정보입니다.
 * 피그마 뷰 - [브랜드] 마이페이지의 상태표를 참고
 */
@Getter
@RequiredArgsConstructor
public enum CampaignStatus {

    // 준비 단계
    DRAFT("임시 저장"),                    // 브랜드가 캠페인 작성 중인 단계

    // 모집 단계
    WAITING_APPROVE("대기 중"),            // 캠페인 제출 후, 승인 대기 전
    OPEN_RESERVED("오픈 예정"),             // 캠페인 승인 후, 오픈 전
    RECRUITING("진행 중"),                 // 캠페인 지원자 모집 시작
    RECRUITMENT_CLOSED("진행 중"),         // 캠페인 지원자 모집 종료

    // 진행 단계
    IN_REVIEW("진행 중"),                  // 캠페인 리뷰 컨텐츠 조회

    // 종료 단계
    COMPLETED("종료");                     // 캠페인 종료

    private final String displayName;
}
