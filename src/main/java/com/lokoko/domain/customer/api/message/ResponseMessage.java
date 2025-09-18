package com.lokoko.domain.customer.api.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    //어드민
    ADMIN_REVIEW_DELETE_SUCCESS("어드민 리뷰 삭제에 성공했습니다"),

    //일반 유저
    CUSTOMER_PROFILE_IMAGE_PRESIGNED_URL_SUCCESS("일반 유저 프로필 이미지 presigend url이 성공적으로 발급되었습니다."),
    CUSTOMER_GET_MYPAGE_INFO_SUCCESS("일반 유저 마이페이지 정보를 성공적으로 불러왔습니다."),
    CUSTOMER_UPDATE_MYPAGE_INFO_SUCCESS("일반 유저 마이페이지 정보를 성공적으로 수정했습니다."),
    CUSTOMER_GET_SNS_STATUS_SUCCESS("일반 유저 SNS 연결 상태를 성공적으로 불러왔습니다");

    private final String message;
}
