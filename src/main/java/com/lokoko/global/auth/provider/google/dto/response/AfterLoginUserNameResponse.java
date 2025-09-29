package com.lokoko.global.auth.provider.google.dto.response;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;

public record AfterLoginUserNameResponse(
        @Schema(requiredMode = REQUIRED, description = "로그인 후 표시되는 이름")
        String displayName
) {
}