package com.lokoko.domain.productReview.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    // Review 관련
    RATING_NOT_FOUND("존재하지 않는 평점입니다."),
    MISSING_MEDIA_TYPE("searchType이 REVIEW일 때 mediaType은 필수입니다."),
    INVALID_MEDIA_TYPE_FORMAT("mediaType은 'video/xxx' 또는 'image/xxx' 형식이어야 합니다."),
    UNSUPPORTED_MEDIA_TYPE("지원하지 않는 파일 형식입니다."),
    TOO_MANY_RECEIPT_IMAGES("영수증 사진은 1개만 업로드 가능합니다."),
    MIXED_MEDIA_TYPE_NOT_ALLOWED("image와 video는 동시에 업로드할 수 없습니다."),
    TOO_MANY_VIDEO_FILES("video는 최대 1개까지만 업로드할 수 있습니다."),
    TOO_MANY_IMAGE_FILES("image는 최대 5개까지만 업로드할 수 있습니다."),
    INVALID_PRESIGNED_URL("Presigned URL 파싱에 실패했습니다."),
    REVIEW_NOT_FOUND("존재하지 않는 리뷰입니다."),
    REVIEW_VIDEO_NOT_FOUND("존재하지 않는 리뷰 영상입니다."),
    RECEIPT_IMAGE_NOT_FOUND("존재하지 않는 영수증 입니다"),
    PRODUCT_IMAGE_NOT_FOUND("제품 이미지가 존재하지 않습니다"),
    REVIEW_DELETE_FORBIDDEN("본인이 작성한 리뷰만 삭제할 수 있습니다"),

    // CampaignReview 관련
    REVIEW_ALREADY_SUBMITTED("이미 해당 단계 리뷰를 제출했습니다");

    private final String message;
}
