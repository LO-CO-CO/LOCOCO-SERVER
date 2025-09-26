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
    MISMATCHED_CONTENT_TYPE("2차 리뷰 콘텐츠 타입은, 1차 리뷰 콘텐츠 타입과 동일해야 합니다."),
    FIRST_REVIEW_NOT_FOUND("1차 리뷰가 있어야 2차 리뷰를 작성할 수 있습니다."),
    CAMPAIGN_REVIEW_NOT_FOUND("캠페인에 대한 리뷰가 존재하지 않습니다"),
    REVISION_REQUEST_NOT_ALLOWED("1차 리뷰가 아닌 리뷰에는 브랜드가 수정 사항을 남길 수 없습니다"),

    MISSING_PLATFORM("컨텐츠 플랫폼 설정이 누락되었습니다."),
    DUPLICATED_PLATFORM("컨텐츠 플랫폼 설정이 중복되었습니다."),
    INVALID_TWO_SET_COMBINATION("두 세트 캠페인은 INSTA_REELS/TIKTOK_VIDEO 조합만 허용됩니다."),
    FIRST_SET_REQUIRED("첫 번째 세트가 비어 있습니다."),
    SECOND_SET_REQUIRED("두 번째 세트가 비어 있습니다."),
    SECOND_SET_NOT_ALLOWED("두 번째 세트는 제공되면 안 됩니다.");

    private final String message;
}
