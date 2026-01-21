package com.lokoko.domain.productReview.domain.repository;

import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import com.lokoko.domain.product.domain.entity.enums.SubCategory;
import com.lokoko.domain.productReview.api.dto.request.RatingCount;
import com.lokoko.domain.productReview.api.dto.response.ImageReviewResponse;
import com.lokoko.domain.productReview.api.dto.response.ImageReviewsProductDetailResponse;
import com.lokoko.domain.productReview.api.dto.response.VideoReviewProductDetailResponse;
import com.lokoko.domain.productReview.api.dto.response.VideoReviewResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ReviewRepositoryCustom {

    Slice<VideoReviewResponse> findVideoReviewsByCategory(MiddleCategory middleCategory, SubCategory subCategory,
                                                          Pageable pageable
    );

    Slice<VideoReviewResponse> findVideoReviewsByCategory(MiddleCategory middleCategory,
                                                          Pageable pageable
    );

    Slice<ImageReviewResponse> findImageReviewsByCategory(MiddleCategory middleCategory, SubCategory subCategory,
                                                          Pageable pageable
    );

    Slice<ImageReviewResponse> findImageReviewsByCategory(MiddleCategory middleCategory,
                                                          Pageable pageable
    );

    ImageReviewsProductDetailResponse findImageReviewsByProductId(Long productId, Long userId, Pageable pageable);


    Slice<VideoReviewResponse> findVideoReviewsByKeyword(List<String> tokens, Pageable pageable);

    Slice<ImageReviewResponse> findImageReviewsByKeyword(List<String> tokens, Pageable pageable);

    Slice<VideoReviewResponse> findVideoReviewsByBrandName(String brandName, Pageable pageable);

    Slice<ImageReviewResponse> findImageReviewsByBrandName(String brandName, Pageable pageable);

    int countProductsByBrandName(String brandName);

    int countReviewsByBrandName(String brandName);

    List<RatingCount> countByProductIdsAndRating(List<Long> productIds);

    VideoReviewProductDetailResponse findVideoReviewsByProductId(Long productId);

}
