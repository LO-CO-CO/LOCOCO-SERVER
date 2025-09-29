package com.lokoko.global.auth.provider.line.dto;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import com.lokoko.global.auth.jwt.dto.LoginResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record LineLoginResponse(
        @Schema(requiredMode = REQUIRED)
        String accessToken,
        @Schema(requiredMode = REQUIRED)
        String refreshToken,
        @Schema(requiredMode = REQUIRED)
        OauthLoginStatus loginStatus
) {
    public static LineLoginResponse of(
            String accessToken,
            String refreshToken,
            OauthLoginStatus loginStatus
    ) {
        return new LineLoginResponse(accessToken, refreshToken, loginStatus);
    }

    public static LineLoginResponse from(LoginResponse loginResponse) {
        return new LineLoginResponse(
                loginResponse.accessToken(),
                loginResponse.refreshToken(),
                loginResponse.loginStatus()
        );
    }
}
