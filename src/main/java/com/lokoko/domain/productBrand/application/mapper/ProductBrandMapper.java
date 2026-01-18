package com.lokoko.domain.productBrand.application.mapper;

import com.lokoko.domain.productBrand.api.dto.response.ProductBrandName;
import com.lokoko.domain.productBrand.api.dto.response.ProductBrandNameListResponse;
import com.lokoko.domain.productBrand.domain.entity.ProductBrand;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductBrandMapper {

    public ProductBrandNameListResponse toBrandNameListResponse(List<ProductBrand> brands) {
        return ProductBrandNameListResponse.builder()
                .brandNames(brands.stream()
                        .map(this::toBrandName)
                        .toList())
                .build();
    }

    private ProductBrandName toBrandName(ProductBrand productBrand) {
        return ProductBrandName.builder()
                .productBrandId(productBrand.getId())
                .productBrandName(productBrand.getBrandName())
                .build();
    }
}
