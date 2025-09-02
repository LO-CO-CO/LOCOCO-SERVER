package com.lokoko.global.auth.jwt.dto;

import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
public record LoginResponse(
        @Schema(requiredMode = REQUIRED)
        String accessToken,
        @Schema(requiredMode = REQUIRED)
        String refreshToken,
        @Schema(requiredMode = REQUIRED)
        OauthLoginStatus loginStatus,
        @Schema(requiredMode = REQUIRED)
        Long userId,
        @Schema(requiredMode = REQUIRED)
        String tokenId,
        @Schema(requiredMode = REQUIRED)
        Role role
) {
    public static LoginResponse of(
            String accessToken,
            String refreshToken,
            OauthLoginStatus loginStatus,
            Long userId,
            String tokenId
    ) {
        return new LoginResponse(accessToken, refreshToken, loginStatus, userId, tokenId, null);
    }

    // role 포함
    public static LoginResponse withRole(String accessToken, String refreshToken,
                                   OauthLoginStatus loginStatus, Long userId,
                                   String tokenId, Role role) {
        return new LoginResponse(accessToken, refreshToken, loginStatus, userId, tokenId, role);
    }
}
