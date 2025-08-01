package com.lokoko.domain.product.application.service;


import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.repository.ProductImageRepository;
import com.lokoko.domain.like.entity.ProductLike;
import com.lokoko.domain.like.repository.ProductLikeRepository;
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
import com.lokoko.domain.product.api.dto.response.SearchProductsResponse;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import com.lokoko.domain.product.domain.entity.enums.SubCategory;
import com.lokoko.domain.product.domain.repository.ProductOptionRepository;
import com.lokoko.domain.product.domain.repository.ProductRepository;
import com.lokoko.domain.product.exception.ProductNotFoundException;
import com.lokoko.domain.product.mapper.ProductMapper;
import com.lokoko.domain.review.api.dto.request.RatingCount;
import com.lokoko.domain.review.domain.entity.enums.Rating;
import com.lokoko.domain.review.domain.repository.ReviewRepository;
import com.lokoko.global.common.response.PageableResponse;
import com.lokoko.global.kuromoji.service.KuromojiService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
    private final ProductLikeRepository productLikeRepository;
    private final ReviewRepository reviewRepository;

    private final KuromojiService kuromojiService;
    private final ProductImageService productImageService;
    private final ProductLikeService productLikeService;
    private final ProductStatsCalculatorService productStatsCalculatorService;

    private final ProductMapper productMapper;

    public SearchProductsResponse search(String keyword, int page, int size, Long userId) {
        List<String> tokens = kuromojiService.tokenize(keyword);
        Slice<Product> slice = productRepository.searchByTokens(tokens, PageRequest.of(page, size));
        List<ProductListItemResponse> products = mapToProductList(slice.getContent(), userId);

        return productMapper.toNameBrandProductResponse(products, keyword, PageableResponse.of(slice));
    }

    // 카테고리 id 로 제품 리스트 조회
    public ProductsByCategoryResponse searchProductsByCategory(MiddleCategory middleCategory, SubCategory subCategory,
                                                               Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<Product> slice = (subCategory == null)
                ? productRepository.findProductsByPopularityAndRating(middleCategory, pageable)
                : productRepository.findProductsByPopularityAndRating(middleCategory, subCategory, pageable);

        Slice<ProductListItemResponse> responseSlice = mapToProductListItems(slice, userId);

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
        Map<Long, List<String>> imageUrlsMap = productImageService.mapAllImageUrls(images);
        String joinedUrls = String.join(",", imageUrlsMap.getOrDefault(productId, List.of()));

        List<RatingCount> stats = reviewRepository.countByProductIdsAndRating(List.of(productId));
        Map<Long, ReviewStats> statsMap = productStatsCalculatorService.calculateProductStats(stats);
        ReviewStats reviewStats = statsMap.getOrDefault(productId, new ReviewStats(0L, 0L, 0.0));
        boolean isLiked = productLikeService.isLiked(productId, userId);

        ProductStatsResponse summary = new ProductStatsResponse(
                joinedUrls,
                reviewStats.reviewCount(),
                reviewStats.avgRating()
        );
        ProductBasicResponse productBasicResponse = ProductBasicResponse.of(
                product,
                summary,
                isLiked
        );

        List<ProductOptionResponse> options = productOptionRepository.findByProduct(product).stream()
                .map(productMapper::toProductOptionResponse)
                .toList();
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

    public Slice<ProductListItemResponse> mapToProductListItems(
            Slice<Product> productSlice,
            Long userId
    ) {
        List<ProductListItemResponse> dtos = mapToProductList(productSlice.getContent(), userId);
        return new SliceImpl<>(dtos, productSlice.getPageable(), productSlice.hasNext());
    }

    private List<ProductListItemResponse> mapToProductList(
            List<Product> products,
            Long userId
    ) {
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .toList();

        Map<Long, String> imageMap = productImageService.mapMainImageUrlsByProductIds(productIds);
        List<RatingCount> stats = reviewRepository.countByProductIdsAndRating(productIds);
        Map<Long, ReviewStats> reviewStatsMap = productStatsCalculatorService.calculateProductStats(stats);

        ReviewStats defaultStats = new ReviewStats(0L, 0L, 0.0);
        Map<Long, ProductStatsResponse> summaryMap = products.stream()
                .collect(Collectors.toMap(
                        Product::getId,
                        p -> {
                            ReviewStats statsForP = reviewStatsMap.getOrDefault(p.getId(), defaultStats);
                            return new ProductStatsResponse(
                                    imageMap.getOrDefault(p.getId(), ""),
                                    statsForP.reviewCount(),
                                    statsForP.avgRating()
                            );
                        }
                ));

        List<ProductLike> likes = Optional.ofNullable(userId)
                .map(productLikeRepository::findAllByUserId)
                .orElseGet(Collections::emptyList);
        Set<Long> likedIds = likes.stream()
                .map(pl -> pl.getProduct().getId())
                .collect(Collectors.toSet());

        return productMapper.toProductListItemResponses(products, summaryMap, likedIds);
    }
}
