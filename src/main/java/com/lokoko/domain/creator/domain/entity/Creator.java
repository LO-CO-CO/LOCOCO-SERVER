package com.lokoko.domain.creator.domain.entity;

import com.lokoko.domain.creator.domain.entity.enums.Gender;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.creator.domain.entity.enums.SkinTone;
import com.lokoko.domain.creator.domain.entity.enums.SkinType;
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

    @Column
    private String instaLink;

    @Column
    private String tiktokLink;

    //최종 전화번호
    public String getCreatorPhoneNumber() {
        return countryCode + phoneNumber;
    }

}
