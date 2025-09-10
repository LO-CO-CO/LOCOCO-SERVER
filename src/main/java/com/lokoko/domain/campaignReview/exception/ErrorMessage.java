package com.lokoko.domain.campaignReview.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    // CampaignReview 관련
    REVIEW_ALREADY_SUBMITTED("이미 해당 라운드 리뷰가 존재합니다."),
    INVALID_CONTENT_IMAGES("리뷰를 업로드하기 위해 최소 1장의 이미지가 필요합니다."),
    BRAND_NOTE_REQUIRED_FOR_SECOND("2차 리뷰에는 브랜드 수정 요청(brandNote)이 필요합니다."),
    MISMATCHED_CONTENT_TYPE("2차 리뷰 콘텐츠 타입은, 1차 리뷰 콘텐츠 타입과 동일해야 합니다.");

    private final String message;
}
