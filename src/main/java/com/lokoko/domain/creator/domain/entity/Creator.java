package com.lokoko.domain.creator.domain.entity;

import com.lokoko.domain.creator.domain.entity.enums.ContentLanguage;
import com.lokoko.domain.creator.domain.entity.enums.CreatorStatus;
import com.lokoko.domain.creator.domain.entity.enums.CreatorType;
import com.lokoko.domain.creator.domain.entity.enums.Gender;
import com.lokoko.domain.creator.domain.entity.enums.SkinTone;
import com.lokoko.domain.creator.domain.entity.enums.SkinType;
import com.lokoko.domain.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "creators")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Creator {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String creatorName;

    @Column
    private String birthDate;

    @Enumerated(EnumType.STRING)
    @Column
    private Gender gender;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String countryCode;

    @Column
    private String phoneNumber;

    @Column
    private String country;

    // 주/도/광역시 등
    @Column
    private String stateOrProvince;

    @Column
    private String cityOrTown;

    @Column
    private String addressLine1;

    @Column
    private String addressLine2;

    @Column
    private String postalCode;

    @Enumerated(EnumType.STRING)
    private SkinType skinType;

    @Enumerated(EnumType.STRING)
    private SkinTone skinTone;

    @Enumerated(EnumType.STRING)
    @Column
    private ContentLanguage contentLanguage = ContentLanguage.ENGLISH;

    @Column
    private String instaLink;

    @Column
    private String tiktokLink;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column
    private CreatorStatus creatorStatus = CreatorStatus.NOT_APPROVED;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column
    private CreatorType creatorType = CreatorType.NORMAL;


    //최종 전화번호
    public String getCreatorPhoneNumber() {
        return countryCode + phoneNumber;
    }

    public void changeCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public void changeFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void changeLastName(String lastName) {
        this.lastName = lastName;
    }

    public void changeCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void changePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void changeCountry(String country) {
        this.country = country;
    }

    public void changeStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    public void changeCityOrTown(String cityOrTown) {
        this.cityOrTown = cityOrTown;
    }

    public void changeAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public void changeAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public void changePostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void changeSkinType(SkinType skinType) {
        this.skinType = skinType;
    }

    public void changeSkinTone(SkinTone skinTone) {
        this.skinTone = skinTone;
    }
}
