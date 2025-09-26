package com.lokoko.global.auth.provider.google.dto;

import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.global.auth.entity.enums.OauthLoginStatus;

public record RoleUpdateResponse(
        String accessToken,
        String refreshToken,
        Role role,
        Long userId,
        OauthLoginStatus loginStatus
) {
}