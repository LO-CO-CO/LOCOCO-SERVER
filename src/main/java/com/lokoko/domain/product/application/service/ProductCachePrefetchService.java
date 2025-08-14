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
public class ProductCachePrefetchService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Cacheable(value = "morePageProducts", key = "#middleCategory.name() + ':popular:p' + #page + ':s' + #size")
    public CachedPopularProductListResponse getPopularProductsForMorePage(
            MiddleCategory middleCategory, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Slice<PopularProductProjection> projectionSlice =
                productRepository.findPopularProductsWithDetails(middleCategory, pageable);

        return productMapper.toCachedPopularProductResponse(
                projectionSlice.getContent(),
                middleCategory,
                PageableResponse.of(projectionSlice)
        );
    }

    @Cacheable(value = "morePageProducts", key = "#middleCategory.name() + ':new:p' + #page + ':s' + #size")
    public CachedNewProductListResponse getNewProductsForMorePage(
            MiddleCategory middleCategory, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Slice<NewProductProjection> projectionSlice =
                productRepository.findNewProductsWithDetails(middleCategory, pageable);

        return productMapper.toNewProductResponse(
                projectionSlice.getContent(),
                middleCategory,
                PageableResponse.of(projectionSlice)
        );
    }
}
