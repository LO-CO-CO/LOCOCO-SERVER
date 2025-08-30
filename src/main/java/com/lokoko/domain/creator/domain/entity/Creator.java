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
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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


//    @NotBlank
//    @Column(nullable = false)
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

//    @NotBlank
//    @Column(nullable = false)
    private String countryCode;

//    @NotBlank
//    @Column(nullable = false)
    private String phoneNumber;

//    @NotBlank
//    @Column(nullable = false)
    private String country;

//    @NotBlank
//    @Column(nullable = false)
    private String postalCode;

    // 주/도/광역시 등
//    @NotBlank
//    @Column(nullable = false)
    private String stateOrProvince;

//    @NotBlank
//    @Column(nullable = false)
    private String city;

//    @NotBlank
//    @Column(nullable = false)
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


}
