package com.lokoko.domain.review.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewRound {
    FIRST, // 1차 리뷰 업로드
    SECOND // 2차 리뷰 업로드
}
