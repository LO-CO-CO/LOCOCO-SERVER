package com.lokoko.global.auth.google.dto;

import com.lokoko.domain.user.domain.entity.enums.Role;

public record RoleUpdateResponse(
        String accessToken,
        String refreshToken,
        Role role,
        Long userId,
        String tokenId
) {
}