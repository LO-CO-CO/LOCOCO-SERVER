package com.lokoko.global.auth.google.dto;

import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.global.auth.exception.InvalidRoleException;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(
        @NotNull
        Role role
) {
    public RoleUpdateRequest {
        // PENDING과 ADMIN은 선택 불가
        if (role == Role.PENDING || role == Role.ADMIN) {
            throw new InvalidRoleException();
        }
    }
}
