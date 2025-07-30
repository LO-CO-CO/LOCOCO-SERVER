package com.lokoko.domain.product.application.service;

import com.lokoko.domain.like.repository.ProductLikeRepository;
import com.lokoko.domain.product.api.dto.response.ProductListItemResponse;
import com.lokoko.domain.product.api.dto.response.ProductStatsResponse;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.mapper.ProductMapper;
import com.lokoko.domain.review.dto.request.RatingCount;
import com.lokoko.domain.review.repository.ReviewRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductService {

    private final ProductLikeRepository productLikeRepository;
    private final ReviewRepository reviewRepository;

    private final ProductImageService productImageService;
    private final ProductStatsCalculatorService productStatsCalculatorService;

    private final ProductMapper productMapper;

    public Slice<ProductListItemResponse> buildMainImageResponseSliceWithReviewData(
            Slice<Product> slice, Long userId
    ) {
        List<ProductListItemResponse> content = buildMainImageResponsesWithReviewData(slice.getContent(), userId);

        return new SliceImpl<>(content, slice.getPageable(), slice.hasNext());
    }

    public List<ProductListItemResponse> buildMainImageResponsesWithReviewData(
            List<Product> products, Long userId
    ) {
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .toList();

        Map<Long, String> imageMap = productImageService.mapMainImageUrlsByProductIds(productIds);

        List<RatingCount> stats = reviewRepository.countByProductIdsAndRating(productIds);
        Map<Long, Long> reviewCountMap = productStatsCalculatorService.calculateReviewCount(stats);
        Map<Long, Long> weightedSumMap = productStatsCalculatorService.calculateWeightedSum(stats);
        Map<Long, Double> avgRatingMap = productStatsCalculatorService.calculateAvgRating(reviewCountMap,
                weightedSumMap);

        Map<Long, ProductStatsResponse> summaryMap = createProductSummaryMap(
                products, imageMap, reviewCountMap, avgRatingMap
        );

        Set<Long> likedIds = (userId != null)
                ? productLikeRepository.findAllByUserId(userId)
                .stream()
                .map(pl -> pl.getProduct().getId())
                .collect(Collectors.toSet())
                : Collections.emptySet();

        return productMapper.toProductListItemResponses(products, summaryMap, likedIds);
    }

    public Map<Long, ProductStatsResponse> createProductSummaryMap(
            List<Product> products,
            Map<Long, String> productIdToImageUrl,
            Map<Long, Long> productIdToReviewCount,
            Map<Long, Double> productIdToAvgRating
    ) {
        Map<Long, ProductStatsResponse> summaryMap = new HashMap<>();
        for (Product product : products) {
            Long productId = product.getId();
            String imageUrl = productIdToImageUrl.getOrDefault(productId, "");
            Long reviewCnt = productIdToReviewCount.getOrDefault(productId, 0L);
            Double avg = productIdToAvgRating.getOrDefault(productId, 0.0);
            summaryMap.put(productId, new ProductStatsResponse(imageUrl, reviewCnt, avg));
        }
        return summaryMap;
    }
}
