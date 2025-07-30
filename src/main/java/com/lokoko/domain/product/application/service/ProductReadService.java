package com.lokoko.domain.product.application.service;


import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.repository.ProductImageRepository;
import com.lokoko.domain.like.service.ProductLikeService;
import com.lokoko.domain.product.api.dto.NewProductProjection;
import com.lokoko.domain.product.api.dto.PopularProductProjection;
import com.lokoko.domain.product.api.dto.ReviewStats;
import com.lokoko.domain.product.api.dto.response.NewProductsByCategoryResponse;
import com.lokoko.domain.product.api.dto.response.PopularProductsByCategoryResponse;
import com.lokoko.domain.product.api.dto.response.ProductBasicResponse;
import com.lokoko.domain.product.api.dto.response.ProductDetailResponse;
import com.lokoko.domain.product.api.dto.response.ProductListItemResponse;
import com.lokoko.domain.product.api.dto.response.ProductOptionResponse;
import com.lokoko.domain.product.api.dto.response.ProductStatsResponse;
import com.lokoko.domain.product.api.dto.response.ProductYoutubeResponse;
import com.lokoko.domain.product.api.dto.response.ProductsByCategoryResponse;
import com.lokoko.domain.product.api.dto.response.RatingPercentResponse;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import com.lokoko.domain.product.domain.entity.enums.SubCategory;
import com.lokoko.domain.product.domain.repository.ProductOptionRepository;
import com.lokoko.domain.product.domain.repository.ProductRepository;
import com.lokoko.domain.product.exception.ProductNotFoundException;
import com.lokoko.domain.product.mapper.ProductMapper;
import com.lokoko.domain.review.dto.request.RatingCount;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.global.common.response.PageableResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductReadService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ReviewRepository reviewRepository;

    private final ProductService productService;
    private final ProductLikeService productLikeService;
    private final ProductStatsCalculatorService productStatsCalculatorService;

    private final ProductMapper productMapper;

    // 카테고리 id 로 제품 리스트 조회
    public ProductsByCategoryResponse searchProductsByCategory(MiddleCategory middleCategory, SubCategory subCategory,
                                                               Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<Product> slice = (subCategory == null)
                ? productRepository.findProductsByPopularityAndRating(middleCategory, pageable)
                : productRepository.findProductsByPopularityAndRating(middleCategory, subCategory, pageable);

        Slice<ProductListItemResponse> responseSlice =
                productService.buildMainImageResponseSliceWithReviewData(slice, userId);

        return productMapper.toCategoryProductPageResponse(
                responseSlice.getContent(),
                middleCategory,
                subCategory,
                PageableResponse.of(responseSlice)
        );
    }

    public NewProductsByCategoryResponse searchNewProductsByCategory(MiddleCategory middleCategory, Long userId,
                                                                     int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<NewProductProjection> projectionSlice =
                productRepository.findNewProductsWithDetails(middleCategory, userId, pageable);

        return productMapper.toCategoryNewProductResponse(
                projectionSlice.getContent(),
                middleCategory,
                PageableResponse.of(projectionSlice)
        );
    }


    public PopularProductsByCategoryResponse searchPopularProductsByCategory(MiddleCategory middleCategory, Long userId,
                                                                             int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<PopularProductProjection> projectionSlice =
                productRepository.findPopularProductsWithDetails(middleCategory, userId, pageable);

        return productMapper.toCategoryPopularProductResponse(
                projectionSlice.getContent(),
                middleCategory,
                PageableResponse.of(projectionSlice)
        );
    }

    public ProductDetailResponse getProductDetail(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        List<ProductImage> images = productImageRepository.findByProductIdIn(List.of(productId));
        Map<Long, List<String>> imageUrlsMap = productService.createProductImageUrlsMap(images);
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
                summary,
                productLikeService.isLiked(productId, userId)
        );

        List<ProductOptionResponse> options = productOptionRepository.findByProduct(product).stream()
                .map(productMapper::toProductOptionResponse)
                .toList();

        boolean isLiked = productLikeService.isLiked(productId, userId);
        List<RatingPercentResponse> starPercent =
                productStatsCalculatorService.calculateRatingPercent(stats);

        return productMapper.toProductDetailResponse(
                productBasicResponse,
                options,
                product,
                starPercent,
                isLiked
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
