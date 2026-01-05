package com.lokoko.domain.user.api.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    ADMIN_REVIEW_DELETE_SUCCESS("어드민 리뷰 삭제에 성공했습니다"),
    ADMIN_CAMPAIGN_APPROVAL_SUCCESS("어드민 캠페인 승인에 성공했습니다."),
    ADMIN_CREATOR_APPROVAL_SUCCESS("어드민 크리에이터 회원가입 승인에 성공했습니다."),
    ADMIN_CAMPAIGN_DELETE_SUCCESS("어드민 캠페인 삭제에 성공했습니다."),
    ADMIN_CAMPAIGN_LIST_GET_SUCCESS("어드민 캠페인 리스트 조회에 성공했습니다."),
    ADMIN_CAMPAIGN_DETAIL_GET_SUCCESS("어드민 캠페인 단건 정보 조회에 성공했습니다."),
    ADMIN_CAMPAIGN_MODIFY_SUCCESS("어드민 캠페인 수정에 성공했습니다."),
    ADMIN_CREATOR_LIST_GET_SUCCESS("어드민 크리에이터 리스트 조회에 성공했습니다."),
    ADMIN_CREATORS_APPROVAL_SUCCESS("어드민 크리에이터 복수 승인에 성공했습니다."),
    ADMIN_CREATORS_DELETE_SUCCESS("어드민 크리에이터 복수 삭제에 성공했습니다."),
    ADMIN_LOGIN_SUCCESS("어드민 로그인에 성공했습니다."),
    ADMIN_REGISTER_SUCCESS("어드민 회원가입에 성공했습니다"),
    USER_ID_CHECK_SUCCESS("사용 가능한 ID입니다.");

    private final String message;
}
