package com.lokoko.domain.product.domain.entity;

import com.lokoko.domain.product.domain.entity.enums.ProductCategory;
import com.lokoko.domain.productBrand.domain.entity.ProductBrand;
import com.lokoko.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Entity
@Table(name = "product")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_brand_id")
    private ProductBrand productBrand;

    @Column(nullable = false)
    private BigDecimal normalPrice;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String productKoreanName;

    @Column
    private String shippingInfo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String productDetail;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String ingredients;

    @Column(nullable = false)
    private Instant manufacturedAt;

    @Column(length = 30, nullable = false)
    private String unit;

    @Column(columnDefinition = "TEXT")
    private String youtubeUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductCategory productCategory;

    public void updateYoutubeUrls(List<String> urls) {
        this.youtubeUrl = String.join(",", urls);
    }

}
