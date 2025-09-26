package com.lokoko.domain.campaignReview.api.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    FIRST_REVIEW_SUCCESS("첫번째 캠페인 리뷰 작성에 성공했습니다."),
    SECOND_REVIEW_SUCCESS("두번째 캠페인 리뷰 작성에 성공했습니다."),
    REVIEW_ABLE_LIST_FETCH_SUCCESS("리뷰 가능 캠페인 조회에 성공했습니다."),
    REVIEW_MEDIA_PRESIGNED_URL_SUCCESS("리뷰 미디어 presignedUrl 발급에 성공했습니다.");

    private final String message;
}
