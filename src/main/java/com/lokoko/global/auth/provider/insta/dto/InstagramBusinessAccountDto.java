package com.lokoko.global.auth.provider.insta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Instagram Business Account 정보 DTO
 * Facebook Page에 연결된 Instagram 비즈니스 계정 정보
 */
public record InstagramBusinessAccountDto(
        @JsonProperty("instagram_business_account")
        InstagramAccount instagramBusinessAccount
) {
    public record InstagramAccount(
            String id,
            String username
    ) {
    }
}
