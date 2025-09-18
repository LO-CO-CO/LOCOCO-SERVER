package com.lokoko.domain.brand.api.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    BRAND_INFO_UPDATE_SUCCESS("브랜드 추가 정보 입력에 성공했습니다."),

    CAMPAIGN_PUBLISH_SUCCESS("캠페인이 발행되었습니다."),
    CAMPAIGN_DRAFT_SUCCESS("캠페인 임시저장이 완료되었습니다."),
    CAMPAIGN_UPDATE_SUCCESS("캠페인 수정이 완료되었습니다."),

    BRAND_PROFILE_IMAGE_PRESIGNED_URL_SUCCESS("브랜드 프로필 이미지 presigend url이 성공적으로 발급되었습니다."),
    BRAND_MYPAGE_INFO_SUCCESS("브랜드 마이페이지 정보를 성공적으로 불러왔습니다."),
    BRAND_UPDATE_MYPAGE_INFO_SUCCESS("브랜드 마이페이지 정보를 성공적으로 수정했습니다.");

    private final String message;
}
