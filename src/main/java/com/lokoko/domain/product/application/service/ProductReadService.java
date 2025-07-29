package com.lokoko.domain.product.application.service;


import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.repository.ProductImageRepository;
import com.lokoko.domain.like.service.ProductLikeService;
import com.lokoko.domain.product.api.dto.response.NewProductProjection;
import com.lokoko.domain.product.api.dto.response.NewProductsByCategoryResponse;
import com.lokoko.domain.product.api.dto.response.PopularProductProjection;
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
import com.lokoko.domain.review.entity.enums.Rating;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.global.common.response.PageableResponse;
import java.util.Arrays;
import java.util.HashMap;
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
        long totalCount = 0L;
        Map<Rating, Long> countMap = new HashMap<>();
        long weightedSum = 0L;

        for (RatingCount rc : stats) {
            Rating rating = rc.rating();
            Long cnt = rc.count();

            countMap.put(rating, cnt);
            totalCount += cnt;
            weightedSum += rating.getValue() * cnt;
        }
        double rawAvg = totalCount == 0
                ? 0.0
                : (double) weightedSum / totalCount;
        double avgRating = Math.round(rawAvg * 10) / 10.0;
        Map<Long, Long> reviewCountMap = Map.of(productId, totalCount);
        Map<Long, Double> avgRatingMap = Map.of(productId, avgRating);
        Map<Long, ProductStatsResponse> summaryMap = productService.createProductSummaryMap(
                List.of(product),
                Map.of(productId, joinedUrls),
                reviewCountMap,
                avgRatingMap
        );
        ProductBasicResponse productBasicResponse = productService.makeProductResponse(
                List.of(product), summaryMap, userId
        ).stream().findFirst().orElseThrow(ProductNotFoundException::new);
        List<ProductOptionResponse> options = productOptionRepository.findByProduct(product).stream()
                .map(productMapper::toProductOptionResponse)
                .toList();

        long cnt5 = countMap.getOrDefault(Rating.FIVE, 0L);
        long cnt4 = countMap.getOrDefault(Rating.FOUR, 0L);
        long cnt3 = countMap.getOrDefault(Rating.THREE, 0L);
        long cnt2 = countMap.getOrDefault(Rating.TWO, 0L);
        long cnt1 = countMap.getOrDefault(Rating.ONE, 0L);
        long pct5 = totalCount == 0 ? 0L : (cnt5 * 100) / totalCount;
        long pct4 = totalCount == 0 ? 0L : (cnt4 * 100) / totalCount;
        long pct3 = totalCount == 0 ? 0L : (cnt3 * 100) / totalCount;
        long pct2 = totalCount == 0 ? 0L : (cnt2 * 100) / totalCount;
        long pct1 = totalCount == 0 ? 0L : (cnt1 * 100) / totalCount;
        List<RatingPercentResponse> starPercent = List.of(
                new RatingPercentResponse(5, pct5),
                new RatingPercentResponse(4, pct4),
                new RatingPercentResponse(3, pct3),
                new RatingPercentResponse(2, pct2),
                new RatingPercentResponse(1, pct1)
        );
        boolean isLiked = productLikeService.isLiked(productId, userId);

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
                .orElse(null);

        return productMapper.toProductDetailYoutubeResponse(urls);
    }
}
