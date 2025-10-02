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
    REVISION_SAVE_SUCCESS("브랜드 수정 사항 임시 저장에 성공했습니다"),

    BRAND_PROFILE_IMAGE_PRESIGNED_URL_SUCCESS("브랜드 프로필 이미지 presigend url이 성공적으로 발급되었습니다."),
    BRAND_MYPAGE_INFO_SUCCESS("브랜드 마이페이지 정보를 성공적으로 불러왔습니다."),
    BRAND_UPDATE_MYPAGE_INFO_SUCCESS("브랜드 마이페이지 정보를 성공적으로 수정했습니다."),
    CAMPAIGN_SIMPLE_INFO_GET_SUCCESS("브랜드 마이페이지에 필요한 캠페인 정보 조회에 성공하였습니다."),
    CREATOR_APPROVE_SUCCESS("크리에이터 승인에 성공하였습니다"),
    CAMPAIGN_APPLICANTS_GET_SUCCESS("캠페인 지원자 목록 조회에 성공했습니다"),

    BRAND_MY_PAGE_CAMPAIGNS_DETAILS_GET_SUCCESS("브랜드 마이페이지 캠페인 리뷰 관련 상세정보 리스트 조회에 성공했습니다"),
    BRAND_MY_PAGE_CAMPAIGNS_GET_SUCCESS("브랜드 마이페이지 캠페인 리스트 조회에 성공했습니다"),
    BRAND_MY_PAGE_REVIEW_DETAIL_GET_SUCCESS("브랜드 마이페이지 캠페인 리뷰 상세 조회에 성공했습니다"),
    BRAND_PROFILE_AND_STATISTICS_GET_SUCCESS("브랜드 마이페이지 프로필 및 통계 정보 조회에 성공했습니다"),
    DRAFT_CAMPAIGN_GET_SUCCESS("임시저장 상태의 캠페인 조회에 성공했습니다."),

    BRAND_DASHBOARD_GET_SUCCESS("브랜드 대시보드 캠페인 리스트 조회 성공했습니다."),
    CREATOR_PERFORMANCE_GET_SUCCESS("캠페인 크리에이터 성과 리스트 조회에 성공했습니다.");

    private final String message;
}
