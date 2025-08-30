package com.lokoko.domain.brand.domain.entity;

import com.lokoko.domain.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "brands")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Brand {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

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


}
