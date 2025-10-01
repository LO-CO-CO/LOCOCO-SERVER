package com.lokoko.global.auth.provider.insta.dto;

import lombok.Builder;

@Builder
public record InstagramConnectionResponse(
        String redirectUrl
) {
    public static InstagramConnectionResponse connected(String returnTo) {
        return InstagramConnectionResponse.builder()
                .redirectUrl(returnTo)
                .build();
    }
}
