package com.lokoko.global.auth.google.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record AfterLoginUserNameResponse(
        @Schema(requiredMode = REQUIRED, description = "로그인 후 표시되는 이름")
        String displayName
) {
}