package com.lokoko.global.utils;

import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.global.auth.exception.AdminPermissionRequiredException;
import com.lokoko.global.auth.exception.InvalidRoleException;

public class AdminValidator {
    public static void validateAdminRole(User user) {
        if (!(user.getRole() == Role.ADMIN)) {
            throw new AdminPermissionRequiredException();
        }
    }

    public static void validateCreatorRole(User user) {
        if (!(user.getRole() == Role.CREATOR)) {
            throw new InvalidRoleException();
        }
    }
}
