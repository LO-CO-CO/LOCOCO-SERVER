package com.lokoko.domain.customer.domain.entity;

import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Entity
@Table(name = "customers")
@DiscriminatorValue("CUSTOMER")
@PrimaryKeyJoinColumn(name = "user_id")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends User {

    @Column(name = "line_id", unique = true)
    private String lineId;

    @Column(name = "google_id", unique = true)
    private String googleId;

    public static Customer createLineUser(String lineUserId, String email, String displayName) {
        return Customer.builder()
                .lineId(lineUserId)
                .email(email)
                .name(displayName)
                .role(Role.CUSTOMER)
                .lastLoginAt(Instant.now())
                .build();
    }

    public static Customer createGoogleUser(String googleUserId, String email, String displayName) {
        return Customer.builder()
                .googleId(googleUserId)
                .email(email)
                .name(displayName)
                .role(Role.CUSTOMER)
                .lastLoginAt(Instant.now())
                .build();
    }
}
