package com.lokoko.domain.review.application.service;

import com.lokoko.domain.image.domain.repository.ReviewImageRepository;
import com.lokoko.domain.review.api.dto.response.MainImageReview;
import com.lokoko.domain.review.api.dto.response.MainImageReviewResponse;
import com.lokoko.domain.review.api.dto.response.MainVideoReview;
import com.lokoko.domain.review.api.dto.response.MainVideoReviewResponse;
import com.lokoko.domain.review.mapper.ReviewMapper;
import com.lokoko.domain.video.domain.repository.ReviewVideoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewCacheService {

    private final ReviewImageRepository reviewImageRepository;
    private final ReviewVideoRepository reviewVideoRepository;
    private final ReviewMapper reviewMapper;

    @Cacheable(value = "popularImageReviews", key = "'all:top:4'")
    public MainImageReviewResponse getPopularImageReviewsFromCache() {

        List<MainImageReview> imageReviews = reviewImageRepository.findMainImageReviewSorted();
        List<MainImageReview> rankedImageReviews = reviewMapper.addRankingToImageReviews(imageReviews);

        return reviewMapper.toMainImageReviewResponse(rankedImageReviews);
    }

    @Cacheable(value = "popularVideoReviews", key = "'all:top:4'")
    public MainVideoReviewResponse getPopularVideoReviewsFromCache() {

        List<MainVideoReview> reviewVideos = reviewVideoRepository.findMainVideoReviewSorted();
        List<MainVideoReview> rankedVideoReviews = reviewMapper.addRankingToVideoReviews(reviewVideos);

        return reviewMapper.toMainVideoReviewResponse(rankedVideoReviews);
    }
}
