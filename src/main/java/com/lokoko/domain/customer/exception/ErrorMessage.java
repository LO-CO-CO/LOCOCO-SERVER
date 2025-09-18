package com.lokoko.domain.customer.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    CUSTOMER_NOT_FOUND("찾을 수 없는 일반 유저입니다.");

    private final String message;
}

