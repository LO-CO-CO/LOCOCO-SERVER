package com.lokoko.domain.campaignReview.api;

import com.lokoko.domain.campaign.api.dto.response.CampaignParticipatedResponse;
import com.lokoko.domain.campaignReview.api.dto.request.FirstReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.request.SecondReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.response.ReviewUploadResponse;
import com.lokoko.domain.campaignReview.api.dto.response.CompletedReviewResponse;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.campaignReview.api.message.ResponseMessage;
import com.lokoko.domain.campaignReview.application.usecase.CampaignReviewUsecase;
import com.lokoko.domain.media.api.dto.request.MediaPresignedUrlRequest;
import com.lokoko.domain.media.api.dto.response.MediaPresignedUrlResponse;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CAMPAIGN REVIEW")
@RestController
@RequestMapping("/api/campaignReviews")
@RequiredArgsConstructor
public class CampaignReviewController {

    private final CampaignReviewUsecase campaignReviewUsecase;

    @Operation(summary = "1차 리뷰 업로드")
    @PostMapping("/{campaignId}/first")
    public ApiResponse<ReviewUploadResponse> uploadFirst(@PathVariable Long campaignId,
                                                         @Parameter(hidden = true) @CurrentUser Long userId,
                                                         @Valid @RequestBody FirstReviewUploadRequest request) {
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.FIRST_REVIEW_SUCCESS.getMessage(),
                campaignReviewUsecase.uploadFirst(userId, campaignId, request));
    }

    @Operation(summary = "2차 리뷰 업로드")
    @PostMapping("/{campaignId}/second")
    public ApiResponse<ReviewUploadResponse> uploadSecond(@PathVariable Long campaignId,
                                                          @Parameter(hidden = true) @CurrentUser Long userId,
                                                          @Valid @RequestBody SecondReviewUploadRequest request) {
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.SECOND_REVIEW_SUCCESS.getMessage(),
                campaignReviewUsecase.uploadSecond(userId, campaignId, request));
    }

    @Operation(summary = "리뷰 업로드 가능 정보 조회 - 특정 캠페인의 특정 라운드 또는 전체")
    @GetMapping("/my/participation/{campaignId}")
    public ApiResponse<CampaignParticipatedResponse> getMyReviewableCampaign(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable Long campaignId,
            @RequestParam(required = false) ReviewRound round
    ) {
        CampaignParticipatedResponse response = campaignReviewUsecase.getMyReviewableCampaign(userId, campaignId, round);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.REVIEW_ABLE_ITEM_FETCH_SUCCESS.getMessage(),
                response);
    }

    @Operation(summary = "리뷰 업로드 가능한 캠페인 목록 조회 - 모든 참여중인 캠페인")
    @GetMapping("/my/participation")
    public ApiResponse<List<CampaignParticipatedResponse>> getMyReviewables(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestParam(required = false) ReviewRound round) {

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.REVIEW_ABLE_LIST_FETCH_SUCCESS.getMessage(),
                campaignReviewUsecase.getMyReviewables(userId, round));
    }

    @Operation(summary = "리뷰 작성 미디어 presignedUrl 발급")
    @PostMapping("/media")
    public ApiResponse<MediaPresignedUrlResponse> createMediaPresignedUrl(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid MediaPresignedUrlRequest request) {
        MediaPresignedUrlResponse response = campaignReviewUsecase.createMediaPresignedUrl(userId, request);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.REVIEW_MEDIA_PRESIGNED_URL_SUCCESS.getMessage(),
                response);
    }

    @Operation(summary = "완료된 캠페인 리뷰 결과 조회 (2차 리뷰)")
    @GetMapping("/{campaignId}/results")
    public ApiResponse<CompletedReviewResponse> getCompletedReviews(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable Long campaignId
    ) {
        CompletedReviewResponse response = campaignReviewUsecase.getCompletedReviews(userId, campaignId);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.COMPLETED_REVIEW_FETCH_SUCCESS.getMessage(),
                response);
    }

}
