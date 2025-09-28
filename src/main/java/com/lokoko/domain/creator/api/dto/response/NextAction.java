package com.lokoko.domain.creator.api.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 크리에이터 캠페인 다음 액션 정의
 */
@Getter
@RequiredArgsConstructor
public enum NextAction {

    VIEW_DETAILS("View Details", "상세 보기"),
    CONFIRM_ADDRESS("Confirm Address", "배송지 확인"),
    UPLOAD_FIRST_REVIEW("Upload 1st Review", "1차 리뷰 업로드"),
    REVISION_REQUESTED("Revision Requested", "수정 요청됨"),
    VIEW_NOTES("View Notes", "브랜드 노트 확인"),
    UPLOAD_SECOND_REVIEW("Upload 2nd Review", "2차 리뷰 업로드"),
    VIEW_RESULTS("View Results", "결과 보기"),
    BRAND_APPROVAL_WAITING("Brand Approval Waiting", "브랜드 승인 대기");

    private final String action;
    private final String description;

    /**
     * 영문 액션명 반환
     */
    public String getAction() {
        return action;
    }

    /**
     * 한글 설명 반환
     */
    public String getDescription() {
        return description;
    }
}