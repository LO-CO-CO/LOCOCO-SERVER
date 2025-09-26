package com.lokoko.domain.productReview.api;

import static com.lokoko.domain.productReview.api.message.ResponseMessage.REVIEW_DELETE_SUCCESS;

import com.lokoko.domain.media.api.dto.request.MediaPresignedUrlRequest;
import com.lokoko.domain.media.api.dto.response.MediaPresignedUrlResponse;
import com.lokoko.domain.productReview.api.dto.request.ReviewReceiptRequest;
import com.lokoko.domain.productReview.api.dto.request.ReviewRequest;
import com.lokoko.domain.productReview.api.dto.response.ImageReviewDetailResponse;
import com.lokoko.domain.productReview.api.dto.response.ImageReviewsProductDetailResponse;
import com.lokoko.domain.productReview.api.dto.response.MainImageReviewResponse;
import com.lokoko.domain.productReview.api.dto.response.MainVideoReviewResponse;
import com.lokoko.domain.productReview.api.dto.response.ReviewReceiptResponse;
import com.lokoko.domain.productReview.api.dto.response.ReviewResponse;
import com.lokoko.domain.productReview.api.dto.response.VideoReviewDetailResponse;
import com.lokoko.domain.productReview.api.dto.response.VideoReviewProductDetailResponse;
import com.lokoko.domain.productReview.api.message.ResponseMessage;
import com.lokoko.domain.productReview.application.service.ReviewDetailsService;
import com.lokoko.domain.productReview.application.service.ReviewReadService;
import com.lokoko.domain.productReview.application.service.ReviewService;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "REVIEW")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewDetailsService reviewDetailsService;
    private final ReviewReadService reviewReadService;

    @Operation(summary = "영수증 presignedUrl 발급")
    @PostMapping("/receipt")
    public ApiResponse<ReviewReceiptResponse> createReceiptPresignedUrl(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid ReviewReceiptRequest request) {
        ReviewReceiptResponse response = reviewService.createReceiptPresignedUrl(userId, request);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.REVIEW_RECEIPT_PRESIGNED_URL_SUCCESS.getMessage(),
                response);
    }


    @Operation(summary = "사진 또는 영상 presignedUrl 발급")
    @PostMapping("/media")
    public ApiResponse<MediaPresignedUrlResponse> createMediaPresignedUrl(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid MediaPresignedUrlRequest request) {
        MediaPresignedUrlResponse response = reviewService.createMediaPresignedUrl(userId, request);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.REVIEW_MEDIA_PRESIGNED_URL_SUCCESS.getMessage(),
                response);
    }

    @Operation(summary = "리뷰 작성")
    @PostMapping("/{productId}")
    public ApiResponse<ReviewResponse> createReview(
            @PathVariable Long productId,
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid ReviewRequest request
    ) {
        ReviewResponse response = reviewService.createReview(productId, userId, request);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.REVIEW_UPLOAD_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "메인페이지에서 이미지 리뷰 조회")
    @GetMapping("/image")
    public ApiResponse<MainImageReviewResponse> getMainImageReviews() {
        MainImageReviewResponse response = reviewReadService.getMainImageReview();
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.MAIN_REVIEW_IMAGE_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "메인페이지에서 영상 리뷰 조회")
    @GetMapping("/video")
    public ApiResponse<MainVideoReviewResponse> getMainVideoReviews() {
        MainVideoReviewResponse response = reviewReadService.getMainVideoReview();
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.MAIN_REVIEW_VIDEO_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "제품 상세 페이지에서 사진 리뷰 조회")
    @GetMapping("/details/image")
    public ApiResponse<ImageReviewsProductDetailResponse> getImageReviewsInProductDetail(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @Parameter(hidden = true) @CurrentUser Long userId
    ) {
        ImageReviewsProductDetailResponse response = reviewReadService.getImageReviewsInProductDetail(productId, page,
                size, userId);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.IMAGE_REVIEW_GET_SUCCESS.getMessage(),
                response);
    }

    @Operation(summary = "제품 상세 페이지에서 영상 리뷰 조회")
    @GetMapping("/details/video")
    public ApiResponse<VideoReviewProductDetailResponse> getVideoReviewsInProductDetail(
            @RequestParam Long productId
    ) {
        VideoReviewProductDetailResponse response = reviewReadService.getVideoReviewsByProduct(productId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.VIDEO_REVIEW_GET_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "영상 리뷰 상세 조회 (가장 마지막 뎁스)")
    @GetMapping("/details/{reviewId}/video")
    public ApiResponse<VideoReviewDetailResponse> getVideoReviewDetails(@PathVariable Long reviewId,
                                                                        @Parameter(hidden = true) @CurrentUser Long userId) {
        VideoReviewDetailResponse response = reviewDetailsService.getVideoReviewDetails(reviewId, userId);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.VIDEO_REVIEW_DETAIL_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "사진 리뷰 상세 조회 (가장 마지막 뎁스")
    @GetMapping("/details/{reviewId}/image")
    public ApiResponse<ImageReviewDetailResponse> getImageReviewDetails(@PathVariable Long reviewId,
                                                                        @Parameter(hidden = true) @CurrentUser Long userId) {
        ImageReviewDetailResponse response = reviewDetailsService.getImageReviewDetails(reviewId, userId);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.IMAGE_REVIEW_DETAIL_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "리뷰 삭제 (일반 유저 및 어드민 모두 가능")
    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(@Parameter(hidden = true) @CurrentUser Long userId,
                                          @PathVariable Long reviewId) {
        reviewService.deleteReview(userId, reviewId);
        return ApiResponse.success(HttpStatus.OK, REVIEW_DELETE_SUCCESS.getMessage());
    }
}
