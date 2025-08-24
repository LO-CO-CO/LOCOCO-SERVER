package com.lokoko.domain.creator.domain.entity;

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
@Table(name = "creators")
@DiscriminatorValue("CREATOR")
@PrimaryKeyJoinColumn(name = "user_id")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Creator extends User {

    @Column(nullable = false)
    private String nickName;

    @Embedded
    private Address address;

    @Column
    private String instaLink;

    @Column
    private String tiktokLink;


    public static Creator of(String email, String name, String nickName, Address address) {
        return Creator.builder()
                .email(email)
                .name(name)
                .nickName(nickName)
                .address(address)
                .role(Role.CREATOR)
                .build();
    }

}
