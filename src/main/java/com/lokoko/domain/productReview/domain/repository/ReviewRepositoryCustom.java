package com.lokoko.domain.productReview.domain.repository;

import com.lokoko.domain.productReview.api.dto.request.RatingCount;
import com.lokoko.domain.productReview.api.dto.response.ImageReviewResponse;
import com.lokoko.domain.productReview.api.dto.response.ImageReviewsProductDetailResponse;
import com.lokoko.domain.productReview.api.dto.response.VideoReviewProductDetailResponse;
import com.lokoko.domain.productReview.api.dto.response.VideoReviewResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ReviewRepositoryCustom {

    ImageReviewsProductDetailResponse findImageReviewsByProductId(Long productId, Long userId, Pageable pageable);
    
    Slice<VideoReviewResponse> findVideoReviewsByBrandName(String brandName, Pageable pageable);

    Slice<ImageReviewResponse> findImageReviewsByBrandName(String brandName, Pageable pageable);

    int countReviewsByBrandName(String brandName);

    List<RatingCount> countByProductIdsAndRating(List<Long> productIds);

    VideoReviewProductDetailResponse findVideoReviewsByProductId(Long productId);

}
