package com.lokoko.domain.review.application.service;

import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import com.lokoko.domain.product.domain.entity.enums.SubCategory;
import com.lokoko.domain.review.api.dto.response.ImageReviewListResponse;
import com.lokoko.domain.review.api.dto.response.ImageReviewResponse;
import com.lokoko.domain.review.api.dto.response.ImageReviewsProductDetailResponse;
import com.lokoko.domain.review.api.dto.response.KeywordImageReviewListResponse;
import com.lokoko.domain.review.api.dto.response.KeywordVideoReviewListResponse;
import com.lokoko.domain.review.api.dto.response.MainImageReviewResponse;
import com.lokoko.domain.review.api.dto.response.MainVideoReviewResponse;
import com.lokoko.domain.review.api.dto.response.VideoReviewListResponse;
import com.lokoko.domain.review.api.dto.response.VideoReviewProductDetailResponse;
import com.lokoko.domain.review.api.dto.response.VideoReviewResponse;
import com.lokoko.domain.review.domain.repository.ReviewRepository;
import com.lokoko.domain.review.mapper.ReviewMapper;
import com.lokoko.global.common.response.PageableResponse;
import com.lokoko.global.kuromoji.service.KuromojiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewReadService {

    private final ReviewRepository reviewRepository;

    private final ReviewCacheService reviewCacheService;
    private final KuromojiService kuromojiService;

    private final ReviewMapper reviewMapper;

    // 카테고리별 영상 리뷰 조회
    public VideoReviewListResponse searchVideoReviewsByCategory(MiddleCategory middleCategory,
                                                                SubCategory subCategory,
                                                                int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Slice<VideoReviewResponse> videoReviews = (subCategory == null)
                ? reviewRepository.findVideoReviewsByCategory(middleCategory, pageable)
                : reviewRepository.findVideoReviewsByCategory(middleCategory, subCategory, pageable);

        PageableResponse pageInfo = PageableResponse.of(videoReviews);

        return reviewMapper.toVideoReviewListResponse(
                videoReviews.getContent(),
                middleCategory,
                subCategory,
                pageInfo
        );

    }

    // 카테고리 별 사진 리뷰조회
    public ImageReviewListResponse searchImageReviewsByCategory(MiddleCategory middleCategory,
                                                                SubCategory subCategory,
                                                                int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Slice<ImageReviewResponse> imageReviews = (subCategory == null)
                ? reviewRepository.findImageReviewsByCategory(middleCategory, pageable)
                : reviewRepository.findImageReviewsByCategory(middleCategory, subCategory, pageable);

        PageableResponse pageInfo = PageableResponse.of(imageReviews);

        return reviewMapper.toImageReviewListResponse(
                imageReviews.getContent(),
                middleCategory,
                subCategory,
                pageInfo
        );
    }

    public KeywordVideoReviewListResponse searchVideoReviewsByKeyword(String keyword, int page, int size) {

        List<String> tokens = kuromojiService.tokenize(keyword);
        Pageable pageable = PageRequest.of(page, size);

        Slice<VideoReviewResponse> videoReviews = reviewRepository.findVideoReviewsByKeyword(tokens,
                pageable);

        return KeywordVideoReviewListResponse.from(keyword, videoReviews);
    }

    public KeywordImageReviewListResponse searchImageReviewsByKeyword(String keyword, int page, int size) {

        List<String> tokens = kuromojiService.tokenize(keyword);
        Pageable pageable = PageRequest.of(page, size);

        Slice<ImageReviewResponse> imageReviews = reviewRepository.findImageReviewsByKeyword(tokens,
                pageable);

        return KeywordImageReviewListResponse.from(keyword, imageReviews);
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
