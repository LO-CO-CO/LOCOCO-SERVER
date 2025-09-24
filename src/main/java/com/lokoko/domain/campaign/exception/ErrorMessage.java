package com.lokoko.domain.campaign.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    CAMPAIGN_NOT_FOUND("존재하지 않는 캠페인입니다."),

    CAMPAIGN_EXPIRED("이미 만료된 캠페인입니다."),
    CAMPAIGN_NOT_BELONG_TO_CREATOR("본인이 참여하고 있는 캠페인이 아닙니다."),

    INVALID_CAMPAIGN_STATUS("해당 캠페인은 조회 가능한 상태가 아닙니다. (미승인 / 작성 상태)"),
    NOT_CAMPAIGN_OWNER("캠페인을 등록한 브랜드가 아닙니다"),
    DRAFT_NOT_FILLED("캠페인 등록을 위한 정보가 모두 입력되지 않았습니다"),
    NOT_EDITABLE_CAMPAIGN("발행된 캠페인은 수정할 수 없습니다."),
    NO_APPLICABLE_CREATOR("승인 가능한 지원자가 없습니다."),
    CAMPAIGN_CAPACITY_EXCEED("캠페인 정원 수를 초과하였습니다."),
    CAMPAIGN_APPLICANT_BULK_UPDATE_FAILED("일부 지원자 승인에 실패했습니다.");

    private final String message;
}
