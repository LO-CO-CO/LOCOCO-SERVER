package com.lokoko.domain.creator.domain.entity;

import com.lokoko.domain.creator.domain.entity.enums.CreatorLevel;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.PersonalColor;
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

}
