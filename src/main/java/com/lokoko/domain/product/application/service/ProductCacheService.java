package com.lokoko.domain.product.application.service;

import com.lokoko.domain.product.api.dto.NewProductProjection;
import com.lokoko.domain.product.api.dto.PopularProductProjection;
import com.lokoko.domain.product.api.dto.response.CachedNewProductListResponse;
import com.lokoko.domain.product.api.dto.response.CachedPopularProductListResponse;
import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import com.lokoko.domain.product.domain.repository.ProductRepository;
import com.lokoko.domain.product.mapper.ProductMapper;
import com.lokoko.global.common.response.PageableResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Cacheable(value = "popularProducts", key = "#middleCategory.name() + ':top4'")
    public CachedPopularProductListResponse getPopularProductsFromCache(MiddleCategory middleCategory) {

        Pageable pageable = PageRequest.of(0, 4);

        Slice<PopularProductProjection> projectionSlice =
                productRepository.findPopularProductsWithDetails(middleCategory, pageable);

        return productMapper.toCachedPopularProductResponse(
                projectionSlice.getContent(),
                middleCategory,
                PageableResponse.of(projectionSlice)
        );
    }

    @Cacheable(value = "newProducts", key = "#middleCategory.name() + ':top4'")
    public CachedNewProductListResponse getNewProductsFromCache(MiddleCategory middleCategory) {

        Pageable pageable = PageRequest.of(0, 4);

        Slice<NewProductProjection> projectionSlice =
                productRepository.findNewProductsWithDetails(middleCategory, pageable);

        return productMapper.toNewProductResponse(
                projectionSlice.getContent(),
                middleCategory,
                PageableResponse.of(projectionSlice)
        );
    }
}
