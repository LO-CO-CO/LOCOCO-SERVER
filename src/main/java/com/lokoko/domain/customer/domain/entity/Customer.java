package com.lokoko.domain.customer.domain.entity;

import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.PersonalColor;
import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.domain.user.domain.entity.enums.SkinTone;
import com.lokoko.domain.user.domain.entity.enums.SkinType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    private PersonalColor personalColor;

    @Enumerated(EnumType.STRING)
    private SkinTone skinTone;

    @Enumerated(EnumType.STRING)
    private SkinType skinType;

    public static Customer createLineUser(String lineUserId, String email, String displayName) {
        return Customer.builder()
                .lineId(lineUserId)
                .email(email)
                .name(displayName)
                .role(Role.CUSTOMER)
                .lastLoginAt(LocalDateTime.now())
                .build();
    }

}
