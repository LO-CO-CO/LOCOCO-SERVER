package com.lokoko.domain.creator.api.dto.response;

import com.lokoko.global.auth.entity.enums.OauthLoginStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record CreatorRegisterCompleteResponse(
        @Schema(requiredMode = REQUIRED, description = "로그인 상태", example = "LOGIN")
        OauthLoginStatus loginStatus
) {
}