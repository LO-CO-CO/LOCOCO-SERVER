package com.lokoko.domain.campaignReview.api.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    FIRST_REVIEW_SUCCESS("첫번째 캠페인 리뷰 작성에 성공했습니다."),
    SECOND_REVIEW_SUCCESS("두번째 캠페인 리뷰 작성에 성공했습니다.");

    private final String message;
}
