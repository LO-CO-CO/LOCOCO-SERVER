package com.lokoko.domain.productReview.application.service;

import com.lokoko.domain.product.domain.repository.ProductRepository;
import com.lokoko.domain.productReview.api.dto.response.BrandImageReviewListResponse;
import com.lokoko.domain.productReview.api.dto.response.BrandVideoReviewListResponse;
import com.lokoko.domain.productReview.api.dto.response.ProductAndReviewCountResponse;
import com.lokoko.domain.productReview.api.dto.response.ImageReviewResponse;
import com.lokoko.domain.productReview.api.dto.response.ImageReviewsProductDetailResponse;
import com.lokoko.domain.productReview.api.dto.response.MainImageReviewResponse;
import com.lokoko.domain.productReview.api.dto.response.MainVideoReviewResponse;
import com.lokoko.domain.productReview.api.dto.response.VideoReviewProductDetailResponse;
import com.lokoko.domain.productReview.api.dto.response.VideoReviewResponse;
import com.lokoko.domain.productReview.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewReadService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    private final ReviewCacheService reviewCacheService;

    public BrandVideoReviewListResponse searchVideoReviewsByBrandName(String brandName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Slice<VideoReviewResponse> videoReviews = reviewRepository.findVideoReviewsByBrandName(brandName, pageable);

        return BrandVideoReviewListResponse.from(brandName, videoReviews);
    }

    public BrandImageReviewListResponse searchImageReviewsByBrandName(String brandName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Slice<ImageReviewResponse> imageReviews = reviewRepository.findImageReviewsByBrandName(brandName, pageable);

        return BrandImageReviewListResponse.from(brandName, imageReviews);
    }

    public ProductAndReviewCountResponse getProductAndReviewCount(String brandName) {
        int productCount = productRepository.countProductsByBrandName(brandName);
        int reviewCount = reviewRepository.countReviewsByBrandName(brandName);

        return ProductAndReviewCountResponse.of(brandName, productCount, reviewCount);
    }

    public ImageReviewsProductDetailResponse getImageReviewsInProductDetail(Long productId, int page,
                                                                            int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findImageReviewsByProductId(productId, userId, pageable);
    }

    public VideoReviewProductDetailResponse getVideoReviewsByProduct(Long productId) {
        return reviewRepository.findVideoReviewsByProductId(productId);
    }


    public MainImageReviewResponse getMainImageReview() {
        return reviewCacheService.getPopularImageReviewsFromCache();
    }

    public MainVideoReviewResponse getMainVideoReview() {
        return reviewCacheService.getPopularVideoReviewsFromCache();
    }


}
