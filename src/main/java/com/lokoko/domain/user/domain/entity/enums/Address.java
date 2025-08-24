package com.lokoko.domain.user.domain.entity.enums;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String addressLine1;

    //선택 주소
    private String addressLine2;

    //우편 번호는 모든 필드에 필수인지 추후 확인 필요
    @Column(nullable = false)
    private String postalCode;
}
