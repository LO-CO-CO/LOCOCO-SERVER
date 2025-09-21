package com.lokoko.domain.campaign.api.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    CAMPAIGN_DETAIL_GET_SUCCESS("캠페인 상세조회에 성공했습니다"),
    CAMPAIGN_MEDIA_PRESIGNED_URL_SUCCESS("캠페인 사진의 Presigned Url이 성공적으로 발급되었습니다."),
    MAIN_PAGE_CAMPAIGNS_GET_SUCCESS("메인페이지에서 캠페인 리스트 조회에 성공했습니다"),
    MAIN_PAGE_UPCOMING_CAMPAIGNS_GET_SUCCESS("메인페이지에서 Opening Soon 캠페인 리스트 조회에 성공했습니다");


    private final String message;
}
