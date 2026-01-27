package com.lokoko.domain.productReview.application.service;


import static com.lokoko.domain.media.application.utils.MediaValidationUtil.validateMediaFiles;
import static com.lokoko.global.utils.AllowedMediaType.ALLOWED_MEDIA_TYPES;

import com.lokoko.domain.like.domain.entity.ReviewLikeCount;
import com.lokoko.domain.like.domain.repository.ReviewLikeCountRepository;
import com.lokoko.domain.like.domain.repository.ReviewLikeRepository;
import com.lokoko.domain.media.api.dto.request.MediaPresignedUrlRequest;
import com.lokoko.domain.media.api.dto.response.MediaPresignedUrlResponse;
import com.lokoko.domain.media.api.dto.response.PresignedUrlResponse;
import com.lokoko.domain.media.application.service.S3Service;
import com.lokoko.domain.media.domain.MediaFile;
import com.lokoko.domain.media.image.domain.entity.ReviewImage;
import com.lokoko.domain.media.image.domain.repository.ReceiptImageRepository;
import com.lokoko.domain.media.image.domain.repository.ReviewImageRepository;
import com.lokoko.domain.media.video.domain.entity.ReviewVideo;
import com.lokoko.domain.media.video.domain.repository.ReviewVideoRepository;
import com.lokoko.domain.product.application.event.PopularProductsCacheEvictEvent;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.repository.ProductRepository;
import com.lokoko.domain.product.exception.ProductNotFoundException;
import com.lokoko.domain.productReview.api.dto.request.ReviewReceiptRequest;
import com.lokoko.domain.productReview.api.dto.request.ReviewRequest;
import com.lokoko.domain.productReview.api.dto.response.ReviewReceiptResponse;
import com.lokoko.domain.productReview.api.dto.response.ReviewResponse;
import com.lokoko.domain.productReview.application.event.PopularReviewsCacheEvictEvent;
import com.lokoko.domain.productReview.domain.entity.Review;
import com.lokoko.domain.productReview.domain.repository.ReviewRepository;
import com.lokoko.domain.productReview.exception.ErrorMessage;
import com.lokoko.domain.productReview.exception.InvalidMediaTypeException;
import com.lokoko.domain.productReview.exception.ReviewNotFoundException;
import com.lokoko.domain.productReview.exception.ReviewPermissionException;
import com.lokoko.domain.productReview.mapper.ReviewMapper;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.global.common.annotation.DistributedLock;
import com.lokoko.global.utils.S3UrlParser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReceiptImageRepository receiptImageRepository;
    private final UserRepository userRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ProductRepository productRepository;
    private final ReviewVideoRepository reviewVideoRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewLikeCountRepository reviewLikeCountRepository;


    private final S3Service s3Service;
    private final ApplicationEventPublisher eventPublisher;
    private final ReviewMapper reviewMapper;

    private static final int MAX_VIDEO_REVIEW_COUNT = 1;
    private static final int MAX_IMAGE_REVIEW_COUNT = 5;

    private static final String VIDEO_URL = "video/";
    private static final String IMAGE_URL = "image/";

    public ReviewReceiptResponse createReceiptPresignedUrl(Long userId,
                                                           ReviewReceiptRequest request) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        String mediaType = request.mediaType();

        // "image/"로 시작하는지, 슬래시가 포함되어 있는지 검사
        if (!(mediaType.startsWith(IMAGE_URL)) || !mediaType.contains("/")) {
            throw new InvalidMediaTypeException(ErrorMessage.INVALID_MEDIA_TYPE_FORMAT);
        }

        //  허용된 mediaType인지 체크
        if (!ALLOWED_MEDIA_TYPES.contains(mediaType)) {
            throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
        }

        PresignedUrlResponse response = s3Service.generatePresignedUrl(mediaType);
        String presignedUrl = response.presignedUrl();

        return reviewMapper.toReviewReceiptUrl(List.of(presignedUrl));
    }

    public MediaPresignedUrlResponse createMediaPresignedUrl(
            Long userId,
            MediaPresignedUrlRequest request) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<String> mediaTypes = request.mediaType();

        boolean hasVideo = mediaTypes.stream().anyMatch(type -> type.startsWith(VIDEO_URL));
        boolean hasImage = mediaTypes.stream().anyMatch(type -> type.startsWith(IMAGE_URL));

        validateMediaTypeAndSize(hasVideo, hasImage, mediaTypes);

        // presigned URL 발급
        List<String> urls = mediaTypes.stream()
                .map(s3Service::generatePresignedUrl)
                .map(PresignedUrlResponse::presignedUrl)
                .toList();

        return reviewMapper.toReviewMediaResponse(urls);
    }

    @DistributedLock(key = "'review:product:' + #productId")
    public ReviewResponse createReview(Long productId, Long userId, ReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // 미디어 검증 (동영상 1개 이하, 이미지 5개 이하, 혼용 불가)
        validateMediaFiles(request.mediaUrl());

        Review review = reviewMapper.toReview(request, user, product);
        Review savedReview = reviewRepository.save(review);

        // 일반 이미지/비디오 저장
        saveMediaFiles(request.mediaUrl(), review);

        // ReviewLikeCount init
        initReviewLikeCountToZero(savedReview);

        publishCacheEvent(product);

        return reviewMapper.toReviewResponse(review);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        if (user.getRole() != Role.ADMIN) {
            if (!userId.equals(review.getAuthor().getId())) {
                throw new ReviewPermissionException();
            }
        }

        publishCacheEvent(review.getProduct());

        deleteAllReferenceOfReview(review);
        reviewRepository.delete(review);
    }

    private static void validateMediaTypeAndSize(boolean hasVideo, boolean hasImage, List<String> mediaTypes) {
        if (hasVideo && hasImage) {
            throw new InvalidMediaTypeException(ErrorMessage.MIXED_MEDIA_TYPE_NOT_ALLOWED);
        }

        // 개수 제한 검증
        if (hasVideo && mediaTypes.size() > MAX_VIDEO_REVIEW_COUNT) {
            throw new InvalidMediaTypeException(ErrorMessage.TOO_MANY_VIDEO_FILES);
        }

        if (hasImage && mediaTypes.size() > MAX_IMAGE_REVIEW_COUNT) {
            throw new InvalidMediaTypeException(ErrorMessage.TOO_MANY_IMAGE_FILES);
        }

        // 허용되지 않은 형식이 있는지 검증
        for (String type : mediaTypes) {
            if (!ALLOWED_MEDIA_TYPES.contains(type)) {
                throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
            }
        }
    }


    private void saveMediaFiles(List<String> mediaUrls, Review review) {
        if (mediaUrls != null) {
            int order = 0;
            for (String url : mediaUrls) {
                MediaFile mediaFile = S3UrlParser.parsePresignedUrl(url);
                if (url.contains(VIDEO_URL)) {
                    ReviewVideo rv = ReviewVideo.createReviewVideo(mediaFile, order++, review);
                    reviewVideoRepository.save(rv);
                } else {
                    ReviewImage ri = ReviewImage.createReviewImage(mediaFile, order++, review);
                    reviewImageRepository.save(ri);
                }
            }
        }
    }

    private void initReviewLikeCountToZero(Review savedReview) {
        reviewLikeCountRepository.save(ReviewLikeCount.init(savedReview.getId()));
    }

    private void publishCacheEvent(Product product) {
        eventPublisher.publishEvent(new PopularProductsCacheEvictEvent(product.getMiddleCategory()));
        eventPublisher.publishEvent(new PopularReviewsCacheEvictEvent());
    }

    public void deleteAllReferenceOfReview(Review review) {
        receiptImageRepository.deleteAllByReview(review);
        reviewImageRepository.deleteAllByReview(review);
        reviewVideoRepository.deleteAllByReview(review);
        reviewLikeRepository.deleteAllByReview(review);
    }
}
