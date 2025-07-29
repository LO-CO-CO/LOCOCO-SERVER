package com.lokoko.domain.review.repository;

import com.lokoko.domain.product.domain.entity.enums.MiddleCategory;
import com.lokoko.domain.product.domain.entity.enums.SubCategory;
import com.lokoko.domain.review.dto.request.RatingCount;
import com.lokoko.domain.review.dto.response.ImageReviewResponse;
import com.lokoko.domain.review.dto.response.ImageReviewsProductDetailResponse;
import com.lokoko.domain.review.dto.response.VideoReviewProductDetailResponse;
import com.lokoko.domain.review.dto.response.VideoReviewResponse;
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

    List<RatingCount> countByProductIdsAndRating(List<Long> productIds);

    VideoReviewProductDetailResponse findVideoReviewsByProductId(Long productId);

}
