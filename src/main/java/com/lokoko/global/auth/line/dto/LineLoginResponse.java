package com.lokoko.global.auth.line.dto;

import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import com.lokoko.global.auth.jwt.dto.LoginResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record LineLoginResponse(
        @Schema(requiredMode = REQUIRED)
        OauthLoginStatus loginStatus
) {
    public static LineLoginResponse of(
            OauthLoginStatus loginStatus
    ) {
        return new LineLoginResponse(loginStatus);
    }

    public static LineLoginResponse from(LoginResponse loginResponse) {
        return new LineLoginResponse(
                loginResponse.loginStatus()
        );
    }
}
