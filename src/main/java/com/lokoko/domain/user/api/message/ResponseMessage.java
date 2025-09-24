package com.lokoko.domain.user.api.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    ADMIN_REVIEW_DELETE_SUCCESS("어드민 리뷰 삭제에 성공했습니다"),

    USER_ID_CHECK_SUCCESS("사용 가능한 ID입니다.");

    private final String message;
}
