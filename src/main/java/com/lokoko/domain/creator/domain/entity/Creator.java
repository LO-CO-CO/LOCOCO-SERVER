package com.lokoko.domain.creator.domain.entity;

import com.lokoko.domain.creator.domain.entity.enums.CreatorLevel;
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
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    @Column(nullable = false)
    private String creatorName;

    @Enumerated(EnumType.STRING)
    private PersonalColor personalColor;

    @Enumerated(EnumType.STRING)
    private SkinTone skinTone;

    @Enumerated(EnumType.STRING)
    private SkinType skinType;

    @Enumerated(EnumType.STRING)
    @Column
    private CreatorLevel creatorLevel;

    @NotBlank
    @Column(nullable = false)
    private String countryCode;

    @NotBlank
    @Column(nullable = false)
    private String phoneNumber;

    @NotBlank
    @Column(nullable = false)
    private String country;

    @NotBlank
    @Column(nullable = false)
    private String postalCode;

    // 주/도/광역시 등
    @NotBlank
    @Column(nullable = false)
    private String stateOrProvince;

    @NotBlank
    @Column(nullable = false)
    private String city;

    @NotBlank
    @Column(nullable = false)
    private String addressLine1;

    private String addressLine2;

    @Column
    private String instaLink;

    @Column
    private String tiktokLink;

    //최종 전화번호
    public String getCreatorPhoneNumber() {
        return countryCode + phoneNumber;
    }

    public static Creator createCreator(
            String email,
            String name,
            String creatorName,
            String countryCode,
            String phoneNumber,
            String country,
            String stateOrProvince,
            String city,
            String postalCode,
            String addressLine1,
            String addressLine2
    ) {
        return Creator.builder()
                .role(Role.CREATOR)
                .email(email)
                .name(name)
                .creatorName(creatorName)
                .countryCode(countryCode)
                .phoneNumber(phoneNumber)
                .country(country)
                .stateOrProvince(stateOrProvince)
                .city(city)
                .postalCode(postalCode)
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .creatorLevel(CreatorLevel.LEVEL_1)
                .build();
    }
}
