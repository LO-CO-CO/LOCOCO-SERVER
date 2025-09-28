package com.lokoko.domain.creatorCampaign.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 클라이언트 요구사항에 맞춘 캠페인 참여 상태
 * 기존의 복잡한 상태들을 클라이언트가 원하는 간단한 6가지 상태로 통합
 */
@Getter
@RequiredArgsConstructor
public enum ParticipationStatus {

    /**
     * 캠페인 지원 후 결과 대기 중
     */
    PENDING("Pending", "View Details"),

    /**
     * 캠페인 당첨 (배송지 확인 필요)
     */
    APPROVED("Approved", "Confirm Address"),

    /**
     * 캠페인 진행 중 (리뷰 업로드, 수정 등)
     * 세부 액션은 리뷰 상태에 따라 동적으로 결정
     */
    ACTIVE("Active", "Upload 1st Review"),

    /**
     * 캠페인 완료 (모든 리뷰 완료)
     */
    COMPLETED("Completed", "View Results"),

    /**
     * 캠페인 만료 (시간 초과)
     */
    EXPIRED("Expired", "View Details"),

    /**
     * 캠페인 거절
     */
    REJECTED("Rejected", "View Details");

    private final String clientStatus;
    private final String defaultAction;

    /**
     * 클라이언트에서 사용할 상태 문자열 반환
     */
    public String getClientStatus() {
        return clientStatus;
    }

    /**
     * 기본 액션 반환 (ACTIVE일 때는 리뷰 상태에 따라 동적으로 결정됨)
     */
    public String getDefaultAction() {
        return defaultAction;
    }

    /**
     * 새로운 6가지 주요 상태들만 반환
     */
    public static List<ParticipationStatus> getMainStatuses() {
        return List.of(PENDING, APPROVED, ACTIVE, COMPLETED, EXPIRED, REJECTED);
    }


    /**
     * 활성 상태들 (승인 후 진행 중인 상태들) 반환
     */
    public static List<ParticipationStatus> getActiveStatuses() {
        return List.of(APPROVED, ACTIVE, COMPLETED);
    }

    /**
     * 종료된 상태들 반환
     */
    public static List<ParticipationStatus> getFinishedStatuses() {
        return List.of(COMPLETED, EXPIRED, REJECTED);
    }
}
