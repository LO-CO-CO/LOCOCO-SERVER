package com.lokoko.global.auth.provider.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    TIKTOK_REDIRECT_URI_GET_SUCCESS("틱톡 redirect uri 반환에 성공했습니다"),
    TIKTOK_CONNECT_SUCCESS("틱톡 계정 연결에 성공했습니다."),
    INSTAGRAM_REDIRECT_URI_GET_SUCCESS("인스타그램 redirect uri 반환에 성공했습니다"),
    INSTAGRAM_CONNECT_SUCCESS("인스타그램 계정 연결에 성공했습니다.");

    private final String message;
}
