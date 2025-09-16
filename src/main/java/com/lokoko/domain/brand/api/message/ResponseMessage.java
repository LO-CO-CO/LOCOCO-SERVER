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
    REVISION_REQUEST_SUCCESS("브랜드 수정사항 전달이 완료되었습니다"),
    REVISION_SAVE_SUCCESS("브랜드 수정 사항 임시 저장에 성공했습니다");

    private final String message;
}
