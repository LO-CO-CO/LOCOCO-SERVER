package com.lokoko.domain.review.service;


import static com.lokoko.global.utils.AllowedMediaType.ALLOWED_MEDIA_TYPES;

import com.lokoko.domain.image.entity.ReceiptImage;
import com.lokoko.domain.image.entity.ReviewImage;
import com.lokoko.domain.image.repository.ReceiptImageRepository;
import com.lokoko.domain.image.repository.ReviewImageRepository;
import com.lokoko.domain.like.repository.ReviewLikeRepository;
import com.lokoko.domain.product.domain.entity.Product;
import com.lokoko.domain.product.domain.entity.ProductOption;
import com.lokoko.domain.product.domain.repository.ProductOptionRepository;
import com.lokoko.domain.product.domain.repository.ProductRepository;
import com.lokoko.domain.product.exception.ProductNotFoundException;
import com.lokoko.domain.product.exception.ProductOptionMismatchException;
import com.lokoko.domain.product.exception.ProductOptionNotFoundException;
import com.lokoko.domain.review.dto.request.ReviewAdminRequest;
import com.lokoko.domain.review.dto.request.ReviewMediaRequest;
import com.lokoko.domain.review.dto.request.ReviewReceiptRequest;
import com.lokoko.domain.review.dto.request.ReviewRequest;
import com.lokoko.domain.review.dto.response.ImageReviewsProductDetailResponse;
import com.lokoko.domain.review.dto.response.MainImageReview;
import com.lokoko.domain.review.dto.response.MainImageReviewResponse;
import com.lokoko.domain.review.dto.response.MainVideoReview;
import com.lokoko.domain.review.dto.response.MainVideoReviewResponse;
import com.lokoko.domain.review.dto.response.ReviewMediaResponse;
import com.lokoko.domain.review.dto.response.ReviewReceiptResponse;
import com.lokoko.domain.review.dto.response.ReviewResponse;
import com.lokoko.domain.review.dto.response.VideoReviewProductDetailResponse;
import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.review.entity.enums.Rating;
import com.lokoko.domain.review.exception.ErrorMessage;
import com.lokoko.domain.review.exception.InvalidMediaTypeException;
import com.lokoko.domain.review.exception.ReceiptImageCountingException;
import com.lokoko.domain.review.exception.ReviewNotFoundException;
import com.lokoko.domain.review.exception.ReviewPermissionException;
import com.lokoko.domain.review.mapper.ReviewMapper;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.entity.enums.Role;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.domain.user.repository.UserRepository;
import com.lokoko.domain.video.domain.entity.ReviewVideo;
import com.lokoko.domain.video.domain.repository.ReviewVideoRepository;
import com.lokoko.global.common.dto.PresignedUrlResponse;
import com.lokoko.global.common.entity.MediaFile;
import com.lokoko.global.common.service.S3Service;
import com.lokoko.global.utils.S3UrlParser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final S3Service s3Service;
    private final ReviewRepository reviewRepository;
    private final ReceiptImageRepository receiptImageRepository;
    private final UserRepository userRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductRepository productRepository;
    private final ReviewVideoRepository reviewVideoRepository;
    private final ReviewLikeRepository reviewLikeRepository;
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

    public ImageReviewsProductDetailResponse getImageReviewsInProductDetail(Long productId, int page,
                                                                            int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findImageReviewsByProductId(productId, userId, pageable);
    }

    @Transactional
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
        if (receiptUrls != null && receiptUrls.size() > 1) {
            throw new ReceiptImageCountingException(ErrorMessage.TOO_MANY_RECEIPT_IMAGES);
        }

        // 미디어 검증 (동영상 1개 이하, 이미지 5개 이하, 혼용 불가)
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

        Review review = reviewMapper.toReview(request, user, product, option);
        reviewRepository.save(review);

        // 영수증 이미지 저장
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

        // 일반 이미지/비디오 저장
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

        return reviewMapper.toReviewResponse(review);
    }

    public MainImageReviewResponse getMainImageReview() {
        List<MainImageReview> reviewImages = reviewImageRepository.findMainImageReviewSorted();
        List<MainImageReview> rankedList = reviewMapper.addRankingToImageReviews(reviewImages);

        return reviewMapper.toMainImageReviewResponse(rankedList);
    }

    public MainVideoReviewResponse getMainVideoReview() {
        List<MainVideoReview> reviewVideo = reviewVideoRepository.findMainVideoReviewSorted();
        List<MainVideoReview> rankedList = reviewMapper.addRankingToVideoReviews(reviewVideo);

        return reviewMapper.toMainVideoReviewResponse(rankedList);
    }

    public VideoReviewProductDetailResponse getVideoReviewsByProduct(Long productId) {
        return reviewRepository.findVideoReviewsByProductId(productId);
    }

    /**
     * Todo: 리뷰 데이터 확보 후, 추후 제거 예정
     */

    @Transactional
    public void createAdminReview(Long productId, Long userId, ReviewAdminRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Product product;
        ProductOption option = null;
        if (request.productOptionId() != null) {
            option = productOptionRepository.findById(request.productOptionId())
                    .orElseThrow(ProductOptionNotFoundException::new);
            product = option.getProduct();
            if (!product.getId().equals(productId)) {
                throw new ProductOptionMismatchException();
            }
        } else {
            product = productRepository.findById(productId)
                    .orElseThrow(ProductNotFoundException::new);
        }
        boolean hasVideo = request.videoUrl() != null && !request.videoUrl().isBlank();
        boolean hasImages = request.imageUrl() != null && !request.imageUrl().isEmpty();
        if (hasVideo && hasImages) {
            throw new InvalidMediaTypeException(ErrorMessage.MIXED_MEDIA_TYPE_NOT_ALLOWED);
        }
        Review.ReviewBuilder builder = Review.builder()
                .author(user)
                .product(product)
                .rating(Rating.fromValue(request.rating()))
                .positiveContent(request.positiveComment())
                .negativeContent(request.negativeComment());
        if (option != null) {
            builder.productOption(option);
        }
        Review review = builder.build();
        reviewRepository.save(review);

        if (request.receiptUrl() != null && !request.receiptUrl().isBlank()) {
            MediaFile receiptFile = MediaFile.builder()
                    .fileUrl(request.receiptUrl())
                    .build();
            ReceiptImage receiptImage = ReceiptImage.builder()
                    .mediaFile(receiptFile)
                    .displayOrder(0)
                    .review(review)
                    .build();
            receiptImageRepository.save(receiptImage);
            review.markReceiptUploaded();
        }

        if (hasVideo) {
            MediaFile videoFile = MediaFile.builder()
                    .fileUrl(request.videoUrl())
                    .build();
            ReviewVideo reviewVideo = ReviewVideo.createReviewVideo(videoFile, 0, review);
            reviewVideoRepository.save(reviewVideo);
        } else if (hasImages) {
            int order = 0;
            for (String url : request.imageUrl()) {
                MediaFile imageFile = MediaFile.builder()
                        .fileUrl(url)
                        .build();
                ReviewImage reviewImage = ReviewImage.builder()
                        .mediaFile(imageFile)
                        .displayOrder(order++)
                        .isMain(order == 0)
                        .review(review)
                        .build();
                reviewImageRepository.save(reviewImage);
                order++;
            }
        }
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        if (user.getRole() == Role.USER) {
            if (!review.getAuthor().getId().equals(userId)) {
                throw new ReviewPermissionException();
            }
        }

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
