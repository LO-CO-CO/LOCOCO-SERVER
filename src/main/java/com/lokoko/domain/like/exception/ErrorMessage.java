package com.lokoko.domain.like.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    SELF_REVIEW_LIKE_NOT_ALLOWED("자신의 리뷰에 좋아요를 누를 수 없습니다.");

    private final String message;
}
