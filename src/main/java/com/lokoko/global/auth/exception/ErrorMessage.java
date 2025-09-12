package com.lokoko.global.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    OAUTH_ERROR("OAuth 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED.value()),

    // LINE 관련
    LINE_TOKEN_REQUEST_FAILED("LINE 토큰 요청에 실패했습니다.", HttpStatus.BAD_GATEWAY.value()),
    LINE_PROFILE_FETCH_FAILED("LINE 프로필 조회에 실패했습니다.", HttpStatus.BAD_GATEWAY.value()),

    // state 관련
    STATE_PARAMETER_REQUIRED("State 매개변수가 필요합니다.", HttpStatus.BAD_REQUEST.value()),
    STATE_PARAMETER_INVALID("유효하지 않거나 만료된 State 매개변수입니다.", HttpStatus.BAD_REQUEST.value()),
    STATE_PARAMETER_EXPIRED("State 매개변수가 만료되었습니다.", HttpStatus.BAD_REQUEST.value()),

    // 권한 관련
    ADMIN_PERMISSION_REQUIRED("관리자 권한이 필요합니다.", HttpStatus.FORBIDDEN.value()),

    // TikTok 관련
    TIKTOK_TOKEN_REQUEST_FAILED("TikTok 토큰 요청에 실패했습니다.", HttpStatus.BAD_GATEWAY.value()),
    TIKTOK_PROFILE_FETCH_FAILED("TikTok 프로필 조회에 실패했습니다.", HttpStatus.BAD_GATEWAY.value()),
    TIKTOK_CONNECTION_FAILED("TikTok 계정 연결에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),

    // 역할 관련
    ROLE_INVALID_TYPE("선택할 수 없는 역할입니다", HttpStatus.BAD_REQUEST.value()),
    ROLE_ALREADY_EXIST("이미 역할이 설정된 사용자입니다", HttpStatus.BAD_REQUEST.value()),
    ROLE_TRANSITION_NOT_ALLOWED ("역할 변경은 PENDING 상태에서만 가능합니다",HttpStatus.BAD_REQUEST.value()),
    ROLE_TRANSITION_NOT_ALLOWED_AFTER_LOGIN("이미 모든 정보 입력이 완료된 사용자는 역할을 변경할 수 없습니다.",HttpStatus.BAD_REQUEST.value());

    private final String message;
    private final int httpStatus;
}
