package com.lokoko.domain.product.service;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.lokoko.domain.image.entity.ProductImage;
import com.lokoko.domain.image.repository.ProductImageRepository;
import com.lokoko.domain.like.repository.ProductLikeRepository;
import com.lokoko.domain.product.dto.response.ProductMainImageResponse;
import com.lokoko.domain.product.dto.response.NameBrandProductResponse;
import com.lokoko.domain.product.dto.response.ProductResponse;
import com.lokoko.domain.product.dto.response.ProductSummary;
import com.lokoko.domain.product.entity.Product;
import com.lokoko.domain.product.repository.ProductRepository;
import com.lokoko.domain.review.dto.request.RatingCount;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.global.common.response.PageableResponse;
import com.lokoko.global.kuromoji.service.KuromojiService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductLikeRepository productLikeRepository;
    private final ReviewRepository reviewRepository;
    private final KuromojiService kuromojiService;

    public NameBrandProductResponse search(String keyword, int page, int size, Long userId) {
        List<String> tokens = kuromojiService.tokenize(keyword);
        Pageable pageable = PageRequest.of(page, size);
        Slice<Product> slice = productRepository.searchByTokens(tokens, pageable);
        Slice<ProductMainImageResponse> responseSlice = buildMainImageResponseSliceWithReviewData(slice, userId);

        return new NameBrandProductResponse(
                keyword,
                responseSlice.getContent(),
                PageableResponse.of(responseSlice)
        );
    }

    public Slice<ProductMainImageResponse> buildMainImageResponseSliceWithReviewData(
            Slice<Product> slice, Long userId
    ) {
        List<ProductMainImageResponse> content = buildMainImageResponsesWithReviewData(slice.getContent(), userId);
        return new SliceImpl<>(content, slice.getPageable(), slice.hasNext());
    }

    public List<ProductMainImageResponse> buildMainImageResponsesWithReviewData(
            List<Product> products, Long userId
    ) {
        List<Long> productIds = products.stream().map(Product::getId).toList();
        Map<Long, String> imageMap = createProductImageMap(productImageRepository.findByProductIdIn(productIds));

        List<RatingCount> stats = reviewRepository.countByProductIdsAndRating(productIds);
        Map<Long, Long> reviewCountMap = new HashMap<>();
        Map<Long, Long> weightedSumMap = new HashMap<>();

        for (RatingCount rc : stats) {
            Long pid = rc.productId();
            int score = rc.rating().getValue();
            Long cnt = rc.count();
            reviewCountMap.merge(pid, cnt, Long::sum);
            weightedSumMap.merge(pid, score * cnt, Long::sum);
        }

        Map<Long, Double> avgMap = productIds.stream().collect(toMap(
                pid -> pid,
                pid -> {
                    long total = reviewCountMap.getOrDefault(pid, 0L);
                    long sum = weightedSumMap.getOrDefault(pid, 0L);
                    double raw = total == 0 ? 0.0 : (double) sum / total;
                    return Math.round(raw * 10) / 10.0;
                }
        ));

        Map<Long, ProductSummary> summaryMap = createProductSummaryMap(products, imageMap, reviewCountMap, avgMap);
        return makeMainImageResponses(products, summaryMap, userId);
    }

    public List<ProductMainImageResponse> makeMainImageResponses(
            List<Product> products, Map<Long, ProductSummary> summaryMap, Long userId
    ) {
        Set<Long> likedIds = productLikeRepository.findAllByUserId(userId).stream()
                .map(pl -> pl.getProduct().getId())
                .collect(Collectors.toSet());

        return products.stream()
                .map(product -> {
                    ProductSummary summary = summaryMap.getOrDefault(
                            product.getId(),
                            new ProductSummary("", 0L, 0.0)
                    );
                    boolean isLiked = likedIds.contains(product.getId());
                    return ProductMainImageResponse.of(product, summary, isLiked);
                })
                .toList();
    }

    public List<ProductResponse> buildProductResponseWithReviewData(
            List<Product> products, Long userId
    ) {
        List<Long> productIds = products.stream().map(Product::getId).toList();
        Map<Long, String> imageMap = createProductImageMap(productImageRepository.findByProductIdIn(productIds));

        List<RatingCount> stats = reviewRepository.countByProductIdsAndRating(productIds);
        Map<Long, Long> reviewCountMap = new HashMap<>();
        Map<Long, Long> weightedSumMap = new HashMap<>();

        for (RatingCount rc : stats) {
            Long pid = rc.productId();
            int score = rc.rating().getValue();
            Long cnt = rc.count();
            reviewCountMap.merge(pid, cnt, Long::sum);
            weightedSumMap.merge(pid, score * cnt, Long::sum);
        }

        Map<Long, Double> avgMap = productIds.stream().collect(toMap(
                pid -> pid,
                pid -> {
                    long total = reviewCountMap.getOrDefault(pid, 0L);
                    long sum = weightedSumMap.getOrDefault(pid, 0L);
                    double raw = total == 0 ? 0.0 : (double) sum / total;
                    return Math.round(raw * 10) / 10.0;
                }
        ));

        Map<Long, ProductSummary> summaryMap = createProductSummaryMap(products, imageMap, reviewCountMap, avgMap);
        return makeProductResponse(products, summaryMap, userId);
    }

    public Slice<ProductResponse> buildProductResponseSliceWithReviewData(
            Slice<Product> slice, Long userId
    ) {
        List<ProductResponse> content = buildProductResponseWithReviewData(slice.getContent(), userId);
        return new SliceImpl<>(content, slice.getPageable(), slice.hasNext());
    }

    public List<ProductResponse> makeProductResponse(
            List<Product> products, Map<Long, ProductSummary> summaryMap, Long userId
    ) {
        Set<Long> likedIds = productLikeRepository.findAllByUserId(userId).stream()
                .map(pl -> pl.getProduct().getId())
                .collect(Collectors.toSet());

        return products.stream()
                .map(product -> {
                    ProductSummary summary = summaryMap.getOrDefault(
                            product.getId(),
                            new ProductSummary("", 0L, 0.0)
                    );
                    boolean isLiked = likedIds.contains(product.getId());
                    return ProductResponse.of(product, summary, isLiked);
                })
                .toList();
    }

    public Map<Long, ProductSummary> createProductSummaryMap(
            List<Product> products,
            Map<Long, String> productIdToImageUrl,
            Map<Long, Long> productIdToReviewCount,
            Map<Long, Double> productIdToAvgRating
    ) {
        Map<Long, ProductSummary> summaryMap = new HashMap<>();
        for (Product product : products) {
            Long productId = product.getId();
            String imageUrl = productIdToImageUrl.getOrDefault(productId, "");
            Long reviewCnt = productIdToReviewCount.getOrDefault(productId, 0L);
            Double avg = productIdToAvgRating.getOrDefault(productId, 0.0);
            summaryMap.put(productId, new ProductSummary(imageUrl, reviewCnt, avg));
        }
        return summaryMap;
    }

    public Map<Long, String> createProductImageMap(List<ProductImage> images) {
        return images.stream().collect(groupingBy(
                img -> img.getProduct().getId(),
                collectingAndThen(toList(), list ->
                        list.stream()
                                .filter(ProductImage::isMain)
                                .findFirst()
                                .orElse(list.get(0))
                                .getUrl()
                )
        ));
    }

    public Map<Long, List<String>> createProductImageUrlsMap(List<ProductImage> images) {
        return images.stream().collect(groupingBy(
                img -> img.getProduct().getId(),
                mapping(ProductImage::getUrl, toList())
        ));
    }
}
