package com.lokoko.domain.brand.domain.entity;

import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    @Column(nullable = false)
    private String brandName;

    @NotBlank
    @Column(nullable = false)
    private String managerName;

    @NotBlank
    @Column(nullable = false)
    private String managerPosition;

    @NotBlank
    @Column(nullable = false)
    private String countryCode;

    @NotBlank
    @Column(nullable = false)
    private String phoneNumber;

    // 시/도
    @NotBlank
    @Column(nullable = false)
    private String province;

    // 시/군/구
    @NotBlank
    @Column(nullable = false)
    private String cityDistrict;

    // 도로명
    @NotBlank
    @Column(nullable = false)
    private String streetName;

    // 건물명/건물번호
    @NotBlank
    @Column(nullable = false)
    private String buildingDetail;

    //최종 전화번호
    public String getBrandPhoneNumber() {
        return countryCode + phoneNumber;
    }

    public static Brand createBrand(
            String brandName,
            String managerName,
            String managerPosition,
            String countryCode,
            String phoneNumber,
            String province,
            String cityDistrict,
            String streetName,
            String buildingDetail
    ) {
        return Brand.builder()
                .role(Role.BRAND)
                .brandName(brandName)
                .managerName(managerName)
                .managerPosition(managerPosition)
                .countryCode(countryCode)
                .phoneNumber(phoneNumber)
                .province(province)
                .cityDistrict(cityDistrict)
                .streetName(streetName)
                .buildingDetail(buildingDetail)
                .build();
    }

}
