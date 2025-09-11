package com.lokoko.domain.creator.api.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    PROFILE_FETCH_SUCCESS("크리에이터 마이페이지 조회 성공"),
    PROFILE_UPDATE_SUCCESS("크리에이터 마이페이지 수정 성공"),
    ADDRESS_CONFIRM_SUCCESS("배송지 확정 성공");

    private final String message;
}
