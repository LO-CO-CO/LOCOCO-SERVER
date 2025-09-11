package com.lokoko.domain.creator.api.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    CREATOR_ID_CHECK_SUCCESS("사용 가능한 크리에이터 ID입니다."),
    CREATOR_INFO_UPDATE_SUCCESS("크리에이터 추가 정보 입력을 성공했습니다."),
    CREATOR_SNS_STATE_SUCCESS("크리에이터 SNS 연결 상태를 성공적으로 불러왔습니다"),
    CREATOR_GET_INFO_REGISTER_SUCCESS("회원가입시에 입력한 필드를 성공적으로 불러왔습니다."),
    CREATOR_LOGIN_SUCCESS("크리에이터 최종 회원가입이 성공했습니다");

    private final String message;
}
