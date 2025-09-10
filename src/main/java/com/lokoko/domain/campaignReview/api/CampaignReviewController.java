package com.lokoko.domain.campaignReview.api;

import com.lokoko.domain.campaignReview.api.dto.request.FirstReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.request.SecondReviewUploadRequest;
import com.lokoko.domain.campaignReview.api.dto.response.ReviewUploadResponse;
import com.lokoko.domain.campaignReview.api.message.ResponseMessage;
import com.lokoko.domain.campaignReview.application.usecase.CampaignReviewUsecase;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
        return ApiResponse.success(HttpStatus.CREATED, ResponseMessage.FIRST_REVIEW_SUCCESS.getMessage(),
                campaignReviewUsecase.uploadFirst(userId, campaignId, request));
    }

    @Operation(summary = "2차 리뷰 업로드")
    @PostMapping("/{campaignId}/second")
    public ApiResponse<ReviewUploadResponse> uploadSecond(@PathVariable Long campaignId,
                                                          @Parameter(hidden = true) @CurrentUser Long userId,
                                                          @Valid @RequestBody SecondReviewUploadRequest request) {
        return ApiResponse.success(HttpStatus.CREATED, ResponseMessage.SECOND_REVIEW_SUCCESS.getMessage(),
                campaignReviewUsecase.uploadSecond(userId, campaignId, request));
    }
}
