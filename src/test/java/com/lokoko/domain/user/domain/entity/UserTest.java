package com.lokoko.domain.user.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lokoko.domain.user.domain.entity.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("유저 동작")
class UserTest {

    @Test
    @DisplayName("createGoogleUser() : 프로필 값과 함께 PENDING 유저를 생성한다")
    void createGoogleUser_initializesPendingProfile() {
        User user = User.createGoogleUser(
                "google-1",
                "user@example.com",
                "Lokoko User",
                "Lokoko",
                "User",
                "https://example.com/profile.png");

        assertThat(user.getGoogleId()).isEqualTo("google-1");
        assertThat(user.getEmail()).isEqualTo("user@example.com");
        assertThat(user.getName()).isEqualTo("Lokoko User");
        assertThat(user.getRole()).isEqualTo(Role.PENDING);
        assertThat(user.getLastLoginAt()).isNotNull();
    }

    @Test
    @DisplayName("updateRole() : 유저 역할을 변경한다")
    void updateRole_updatesUserRole() {
        User user = User.createLineUser("line-1", "user@example.com", "Lokoko User");

        user.updateRole(Role.CREATOR);

        assertThat(user.getRole()).isEqualTo(Role.CREATOR);
    }

    @Test
    @DisplayName("assertRole() : 역할이 일치하면 통과한다")
    void assertRole_allowsMatchingRole() {
        User user = User.createLineUser("line-1", "user@example.com", "Lokoko User");
        user.updateRole(Role.CREATOR);

        user.assertRole(Role.CREATOR, IllegalStateException::new);
    }

    @Test
    @DisplayName("assertRole() : 역할이 다르면 전달한 예외를 던진다")
    void assertRole_throwsSupplierExceptionWhenRoleDiffers() {
        User user = User.createLineUser("line-1", "user@example.com", "Lokoko User");

        assertThatThrownBy(() -> user.assertRole(Role.CREATOR, IllegalStateException::new))
                .isInstanceOf(IllegalStateException.class);
    }
}
