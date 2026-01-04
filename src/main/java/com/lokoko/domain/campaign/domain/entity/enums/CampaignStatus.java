package com.lokoko.domain.campaign.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 브랜드 관점에서 보는 캠페인의 상태에 대한 정보입니다.
 * 피그마 뷰 - [브랜드] 마이페이지의 상태표를 참고
 */
@Getter
@RequiredArgsConstructor
public enum CampaignStatus {

    // 캠페인 시작 전
    DRAFT("임시 저장"),                    // 브랜드가 캠페인 작성 중인 단계

    // 대기 단계
    WAITING_APPROVAL("대기 중"),            // 캠페인 제출 후, 승인 대기 전
    OPEN_RESERVED("오픈 예정"),             // 캠페인 승인 후, 오픈 전

    // 캠페인 진행 단계
    RECRUITING("모집 중"),                 // 캠페인 지원자 모집 시작
    RECRUITMENT_CLOSED("모집 완료"),       // 캠페인 지원자 모집 종료
    IN_REVIEW("리뷰 진행 중"),                  // 캠페인 리뷰 컨텐츠 조회

    // 종료 단계
    COMPLETED("종료");                     // 캠페인 종료

    private final String displayName;

    public static List<CampaignStatus> getApprovedStatuses() {
        return List.of(OPEN_RESERVED, RECRUITING , RECRUITMENT_CLOSED, IN_REVIEW, COMPLETED);
    }
}
