package com.lokoko.domain.user.domain.entity;

import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.customer.domain.entity.Customer;
import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.domain.user.domain.entity.enums.UserStatus;
import com.lokoko.global.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Entity
@SuperBuilder
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(name = "line_id", unique = true)
    private String lineId;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Customer customer;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Creator creator;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Brand brand;

    @Column
    private String email;

    @Column
    private String name;

    @Column
    private String profileImageUrl;

    @Column
    private Instant lastLoginAt;

    @Column(nullable = false, length = 20)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Role role = Role.PENDING;

    @Column(nullable = false, length = 20)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;


    public void updateLastLoginAt() {
        this.lastLoginAt = Instant.now();
    }

    public void updateDisplayName(String name) {
        this.name = name;
    }

    public void updateEmail(String email) {
        this.email = email;
    }


    public static User createLineUser(String lineUserId, String email, String displayName) {
        return User.builder()
                .lineId(lineUserId)
                .email(email)
                .name(displayName)
                .role(Role.PENDING)
                .lastLoginAt(Instant.now())
                .build();
    }

    public static User createGoogleUser(String googleUserId, String email, String displayName) {
        return User.builder()
                .googleId(googleUserId)
                .email(email)
                .name(displayName)
                .role(Role.PENDING)
                .lastLoginAt(Instant.now())
                .build();
    }


    public void assignCustomer(Customer customer) {
        this.customer = customer;
    }

    public void assignCreator(Creator creator) {
        this.creator = creator;
    }

    public void assignBrand(Brand brand) {
        this.brand = brand;
    }

    public void updateRole(Role role) {
        this.role = role;
    }
}
