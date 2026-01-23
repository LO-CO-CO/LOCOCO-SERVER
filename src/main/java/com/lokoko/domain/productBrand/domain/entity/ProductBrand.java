package com.lokoko.domain.productBrand.domain.entity;

import com.lokoko.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Getter
@Entity
@Table(name = "product_brand")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductBrand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_brand_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String brandName;

}
