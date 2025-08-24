package com.lokoko.domain.brand.domain.entity;

import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.Address;
import com.lokoko.domain.user.domain.entity.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@Table(name = "brands")
@DiscriminatorValue("BRAND")
@PrimaryKeyJoinColumn(name = "user_id")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand extends User {

    @Column(nullable = false)
    private String brandName;

    @Column
    private String manager;

    @Embedded
    private Address address;

    public static Brand of(String email, String name, String brandName, Address address) {
        return Brand.builder()
                .email(email)
                .name(name)
                .brandName(brandName)
                .address(address)
                .role(Role.BRAND)
                .build();
    }
}
