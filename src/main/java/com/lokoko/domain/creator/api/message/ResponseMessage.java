package com.lokoko.domain.creator.api.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    // 회원가입 관련
    CREATOR_ID_CHECK_SUCCESS("사용 가능한 크리에이터 ID입니다."),
    CREATOR_INFO_UPDATE_SUCCESS("크리에이터 추가 정보 입력을 성공했습니다."),
    CREATOR_GET_SNS_STATUS_SUCCESS("크리에이터 SNS 연결 상태를 성공적으로 불러왔습니다"),
    CREATOR_GET_INFO_REGISTER_SUCCESS("회원가입시에 입력한 필드를 성공적으로 불러왔습니다."),
    CREATOR_LOGIN_SUCCESS("크리에이터 최종 회원가입이 성공했습니다"),

    // 마이페이지 관련
    PROFILE_FETCH_SUCCESS("크리에이터 마이페이지 조회 성공"),
    PROFILE_UPDATE_SUCCESS("크리에이터 마이페이지 수정 성공"),
    ADDRESS_CONFIRM_SUCCESS("배송지 확정 성공"),
    MY_CAMPAIGN_FETCH_SUCCESS("참여 캠페인 조회 성공"),
    PROFILE_FETCH_ADDRESS_SUCCESS("크리에이터 배송지 조회 성공");

    private final String message;
}
