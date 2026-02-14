package com.lokoko.domain.product.application.service;

import com.lokoko.domain.media.image.domain.entity.ProductImage;
import com.lokoko.domain.media.image.domain.repository.ProductImageRepository;
import com.lokoko.domain.product.api.dto.ReviewStats;
import com.lokoko.domain.product.api.dto.response.NewProductsByCategoryResponse;
import com.lokoko.domain.product.api.dto.response.PopularProductsByCategoryResponse;
import com.lokoko.domain.product.api.dto.response.ProductBasicResponse;
import com.lokoko.domain.product.api.dto.response.ProductDetailResponse;
import com.lokoko.domain.product.api.dto.response.ProductStatsResponse;
import com.lokoko.domain.product.api.dto.response.ProductYoutubeResponse;
import com.lokoko.domain.product.api.dto.response.RatingPercentResponse;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.entity.enums.ProductCategory;
import com.lokoko.domain.product.domain.repository.ProductRepository;
import com.lokoko.domain.product.exception.ProductNotFoundException;
import com.lokoko.domain.product.mapper.ProductMapper;
import com.lokoko.domain.productReview.api.dto.request.RatingCount;
import com.lokoko.domain.productReview.domain.repository.ReviewRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductReadService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ReviewRepository reviewRepository;

    private final ProductImageService productImageService;
    private final ProductStatsCalculatorService productStatsCalculatorService;

    private final ProductMapper productMapper;

    public NewProductsByCategoryResponse searchNewProductsByCategory(ProductCategory productCategory) {
        return new NewProductsByCategoryResponse(
                productRepository.findNewProductsWithDetails(productCategory)
        );
    }

    @Cacheable(value = "popularProducts", key = "#productCategory != null ? #productCategory.name() : 'ALL'")
    public PopularProductsByCategoryResponse searchPopularProductsByCategory(ProductCategory productCategory) {
        return new PopularProductsByCategoryResponse(
                productRepository.findPopularProductsWithDetails(productCategory)
        );
    }

    public ProductDetailResponse getProductDetail(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        List<ProductImage> images = productImageRepository.findByProductIdIn(List.of(productId));
        Map<Long, List<String>> imageUrlsMap = productImageService.mapAllImageUrls(images);
        String joinedUrls = String.join(",", imageUrlsMap.getOrDefault(productId, List.of()));

        List<RatingCount> stats = reviewRepository.countByProductIdsAndRating(List.of(productId));
        Map<Long, ReviewStats> statsMap = productStatsCalculatorService.calculateProductStats(stats);
        ReviewStats reviewStats = statsMap.getOrDefault(productId, new ReviewStats(0L, 0L, 0.0));
        ProductStatsResponse summary = new ProductStatsResponse(
                joinedUrls,
                reviewStats.reviewCount(),
                reviewStats.avgRating()
        );
        ProductBasicResponse productBasicResponse = ProductBasicResponse.of(
                product,
                summary
        );

        List<RatingPercentResponse> starPercent =
                productStatsCalculatorService.calculateRatingPercent(stats);

        return productMapper.toProductDetailResponse(
                productBasicResponse,
                product,
                starPercent
        );
    }

    public ProductYoutubeResponse getProductDetailYoutube(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        List<String> urls = Optional.ofNullable(product.getYoutubeUrl())
                .filter(u -> !u.isBlank())
                .map(u -> Arrays.stream(u.split(","))
                        .map(String::trim)
                        .toList())
                .orElseGet(List::of);

        return productMapper.toProductDetailYoutubeResponse(urls);
    }
}
