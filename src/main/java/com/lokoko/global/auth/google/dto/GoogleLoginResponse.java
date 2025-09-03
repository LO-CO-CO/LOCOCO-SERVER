package com.lokoko.global.auth.google.dto;

import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import com.lokoko.global.auth.jwt.dto.LoginResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record GoogleLoginResponse(
        @Schema(requiredMode = REQUIRED)
        OauthLoginStatus loginStatus,
        @Schema(requiredMode = REQUIRED)
        Role role
) {

    public static GoogleLoginResponse from(LoginResponse loginResponse) {
        return new GoogleLoginResponse(
                loginResponse.loginStatus(),
                loginResponse.role()
        );
    }
}