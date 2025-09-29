package com.lokoko.global.auth.provider.insta.dto;

import lombok.Builder;

@Builder
public record InstagramConnectionResponse(
        String instagramUserId
) {
    public static InstagramConnectionResponse connected(String instaUserId) {
        return InstagramConnectionResponse.builder()
                .instagramUserId(instaUserId)
                .build();
    }
}
