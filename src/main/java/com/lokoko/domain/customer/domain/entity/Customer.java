package com.lokoko.domain.customer.domain.entity;

import com.lokoko.domain.creator.domain.entity.enums.ContentLanguage;
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

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "customers")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Customer {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String customerName;

    @Column
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column
    private Gender gender;

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
    private String instaUserId;

    @Column
    private String tikTokUserId;

    public String getCreatorPhoneNumber() {
        return countryCode + phoneNumber;
    }

    public void assignCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void assignBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void assignGender(Gender gender) {
        this.gender = gender;
    }

    public void assignCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void assignPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void assignCountry(String country) {
        this.country = country;
    }

    public void assignStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    public void assignCityOrTown(String cityOrTown) {
        this.cityOrTown = cityOrTown;
    }

    public void assignAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public void assignAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public void assignPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void assignSkinType(SkinType skinType) {
        this.skinType = skinType;
    }

    public void assignSkinTone(SkinTone skinTone) {
        this.skinTone = skinTone;
    }

    public void assignContentLanguage(ContentLanguage contentLanguage) {
        this.contentLanguage = contentLanguage;
    }
}
