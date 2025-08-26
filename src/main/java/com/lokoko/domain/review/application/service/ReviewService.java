package com.lokoko.domain.review.application.service;


import static com.lokoko.global.utils.AllowedMediaType.ALLOWED_MEDIA_TYPES;

import com.lokoko.domain.image.domain.entity.ReceiptImage;
import com.lokoko.domain.image.domain.entity.ReviewImage;
import com.lokoko.domain.image.domain.repository.ReceiptImageRepository;
import com.lokoko.domain.image.domain.repository.ReviewImageRepository;
import com.lokoko.domain.like.domain.repository.ReviewLikeRepository;
import com.lokoko.domain.product.application.event.PopularProductsCacheEvictEvent;
import com.lokoko.domain.review.application.event.PopularReviewsCacheEvictEvent;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.entity.ProductOption;
import com.lokoko.domain.product.domain.repository.ProductOptionRepository;
import com.lokoko.domain.product.domain.repository.ProductRepository;
import com.lokoko.domain.product.exception.ProductNotFoundException;
import com.lokoko.domain.product.exception.ProductOptionMismatchException;
import com.lokoko.domain.product.exception.ProductOptionNotFoundException;
import com.lokoko.domain.review.api.dto.request.ReviewMediaRequest;
import com.lokoko.domain.review.api.dto.request.ReviewReceiptRequest;
import com.lokoko.domain.review.api.dto.request.ReviewRequest;
import com.lokoko.domain.review.api.dto.response.ReviewMediaResponse;
import com.lokoko.domain.review.api.dto.response.ReviewReceiptResponse;
import com.lokoko.domain.review.api.dto.response.ReviewResponse;
import com.lokoko.domain.review.domain.entity.Review;
import com.lokoko.domain.review.domain.repository.ReviewRepository;
import com.lokoko.domain.review.exception.ErrorMessage;
import com.lokoko.domain.review.exception.InvalidMediaTypeException;
import com.lokoko.domain.review.exception.ReceiptImageCountingException;
import com.lokoko.domain.review.exception.ReviewNotFoundException;
import com.lokoko.domain.review.exception.ReviewPermissionException;
import com.lokoko.domain.review.mapper.ReviewMapper;
import com.lokoko.domain.user.domain.entity.User;
import com.lokoko.domain.user.domain.entity.enums.Role;
import com.lokoko.domain.user.domain.repository.UserRepository;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.domain.video.domain.entity.ReviewVideo;
import com.lokoko.domain.video.domain.repository.ReviewVideoRepository;
import com.lokoko.global.common.annotation.DistributedLock;
import com.lokoko.global.common.dto.PresignedUrlResponse;
import com.lokoko.global.common.entity.MediaFile;
import com.lokoko.global.common.service.S3Service;
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
    private final ProductOptionRepository productOptionRepository;
    private final ProductRepository productRepository;
    private final ReviewVideoRepository reviewVideoRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    private final S3Service s3Service;
    private final ReviewCacheService reviewCacheService;

    private final ApplicationEventPublisher eventPublisher;
    private final ReviewMapper reviewMapper;

    public ReviewReceiptResponse createReceiptPresignedUrl(Long userId,
                                                           ReviewReceiptRequest request) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        String mediaType = request.mediaType();

        // "image/"로 시작하는지, 슬래시가 포함되어 있는지 검사
        if (!(mediaType.startsWith("image/")) || !mediaType.contains("/")) {
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

    public ReviewMediaResponse createMediaPresignedUrl(
            Long userId,
            ReviewMediaRequest request) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<String> mediaTypes = request.mediaType();

        boolean hasVideo = mediaTypes.stream().anyMatch(type -> type.startsWith("video/"));
        boolean hasImage = mediaTypes.stream().anyMatch(type -> type.startsWith("image/"));

        if (hasVideo && hasImage) {
            throw new InvalidMediaTypeException(ErrorMessage.MIXED_MEDIA_TYPE_NOT_ALLOWED);
        }

        // 개수 제한 검증
        if (hasVideo && mediaTypes.size() > 1) {
            throw new InvalidMediaTypeException(ErrorMessage.TOO_MANY_VIDEO_FILES);
        }

        if (hasImage && mediaTypes.size() > 5) {
            throw new InvalidMediaTypeException(ErrorMessage.TOO_MANY_IMAGE_FILES);
        }

        // 허용되지 않은 형식이 있는지 검증
        for (String type : mediaTypes) {
            if (!ALLOWED_MEDIA_TYPES.contains(type)) {
                throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
            }
        }

        // presigned URL 발급
        List<String> urls = mediaTypes.stream()
                .map(s3Service::generatePresignedUrl)
                .map(PresignedUrlResponse::presignedUrl)
                .toList();

        return reviewMapper.toReviewMediaResponse(urls);
    }

    @DistributedLock(key = "'review:product:' + #productId")
    public ReviewResponse createReview(
            Long productId,
            Long userId,
            ReviewRequest request
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        ProductOption option = null;
        if (request.productOptionId() != null) {
            option = productOptionRepository.findById(request.productOptionId())
                    .orElseThrow(ProductOptionNotFoundException::new);
            if (!option.getProduct().getId().equals(productId)) {
                throw new ProductOptionMismatchException();
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<String> receiptUrls = request.receiptUrl();
        List<String> mediaUrls = request.mediaUrl();

        // 영수증 1장 초과 검증
        validateReceiptCount(receiptUrls);

        // 미디어 검증 (동영상 1개 이하, 이미지 5개 이하, 혼용 불가)
        validateMediaFiles(mediaUrls);

        Review review = reviewMapper.toReview(request, user, product, option);
        reviewRepository.save(review);

        // 영수증 이미지 저장
        saveReceiptImages(receiptUrls, review);

        // 일반 이미지/비디오 저장
        saveMediaFiles(mediaUrls, review);

        eventPublisher.publishEvent(new PopularProductsCacheEvictEvent(product.getMiddleCategory()));
        eventPublisher.publishEvent(new PopularReviewsCacheEvictEvent());

        return reviewMapper.toReviewResponse(review);
    }

    private void saveMediaFiles(List<String> mediaUrls, Review review) {
        if (mediaUrls != null) {
            int order = 0;
            for (String url : mediaUrls) {
                MediaFile mediaFile = S3UrlParser.parsePresignedUrl(url);
                if (url.contains("/video/")) {
                    ReviewVideo rv = ReviewVideo.createReviewVideo(mediaFile, order++, review);
                    reviewVideoRepository.save(rv);
                } else {
                    ReviewImage ri = ReviewImage.createReviewImage(mediaFile, order++, review);
                    reviewImageRepository.save(ri);
                }
            }
        }
    }

    private void saveReceiptImages(List<String> receiptUrls, Review review) {
        if (receiptUrls != null) {
            int order = 0;
            for (String url : receiptUrls) {
                MediaFile mediaFile = S3UrlParser.parsePresignedUrl(url);
                ReceiptImage ri = ReceiptImage.builder()
                        .mediaFile(mediaFile)
                        .displayOrder(order++)
                        .review(review)
                        .build();
                receiptImageRepository.save(ri);
            }

            review.markReceiptUploaded();
        }
    }

    private static void validateMediaFiles(List<String> mediaUrls) {
        if (mediaUrls != null && !mediaUrls.isEmpty()) {
            long videoCount = mediaUrls.stream().filter(url -> url.contains("/video/")).count();
            long imageCount = mediaUrls.stream().filter(url -> url.contains("/image/")).count();

            if (videoCount > 0 && imageCount > 0) {
                throw new InvalidMediaTypeException(ErrorMessage.MIXED_MEDIA_TYPE_NOT_ALLOWED);
            }
            if (videoCount > 1) {
                throw new InvalidMediaTypeException(ErrorMessage.TOO_MANY_VIDEO_FILES);
            }
            if (imageCount > 5) {
                throw new InvalidMediaTypeException(ErrorMessage.TOO_MANY_IMAGE_FILES);
            }
        }
    }

    private static void validateReceiptCount(List<String> receiptUrls) {
        if (receiptUrls != null && receiptUrls.size() > 1) {
            throw new ReceiptImageCountingException(ErrorMessage.TOO_MANY_RECEIPT_IMAGES);
        }
    }


    @Transactional
    public void deleteReview(Long userId, Long reviewId) {

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        if (user.getRole() == Role.CUSTOMER) {
            if (!review.getAuthor().getId().equals(userId)) {
                throw new ReviewPermissionException();
            }
        }

        eventPublisher.publishEvent(new PopularProductsCacheEvictEvent(review.getProduct().getMiddleCategory()));
        eventPublisher.publishEvent(new PopularReviewsCacheEvictEvent());

        deleteAllReferenceOfReview(review);
        reviewRepository.delete(review);
    }

    public void deleteAllReferenceOfReview(Review review) {
        receiptImageRepository.deleteAllByReview(review);
        reviewImageRepository.deleteAllByReview(review);
        reviewVideoRepository.deleteAllByReview(review);
        reviewLikeRepository.deleteAllByReview(review);
    }


}
