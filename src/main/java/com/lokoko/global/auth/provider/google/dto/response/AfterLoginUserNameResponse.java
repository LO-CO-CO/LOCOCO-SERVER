package com.lokoko.global.auth.provider.google.dto.response;

import com.lokoko.domain.user.domain.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record AfterLoginUserNameResponse(
        @Schema(requiredMode = REQUIRED, description = "로그인 후 표시되는 이름")
        String displayName,
        @Schema(requiredMode = REQUIRED, description = "해당 사용자 역할", example = "BRAND")
        Role role
) {
}