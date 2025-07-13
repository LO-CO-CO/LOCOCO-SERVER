package com.lokoko.domain.review.service;

import com.lokoko.domain.like.repository.ReviewLikeRepository;
import com.lokoko.domain.review.dto.response.VideoReviewDetailResponse;
import com.lokoko.domain.review.exception.ReviewNotFoundException;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.domain.video.entity.ReviewVideo;
import com.lokoko.domain.video.repository.ReviewVideoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewDetailsService {

    private final ReviewRepository reviewRepository;
    private final ReviewVideoRepository reviewVideoRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    public List<VideoReviewDetailResponse> getVideoReviewDetails(Long reviewId) {
        reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        List<ReviewVideo> videos = reviewVideoRepository.findAllByReviewId(reviewId);
        return videos.stream()
                .map(video -> {
                    long likes = reviewLikeRepository.countByReviewId(reviewId);
                    return VideoReviewDetailResponse.from(video, likes);
                })
                .toList();
    }
}
