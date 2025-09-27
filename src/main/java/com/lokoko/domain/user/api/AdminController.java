package com.lokoko.domain.user.api;

import com.lokoko.domain.user.api.message.ResponseMessage;
import com.lokoko.domain.user.application.usecase.AdminUsecase;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@Tag(name = "ADMIN")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminUsecase adminUsecase;

    @Operation(summary = "어드민 리뷰 삭제")
    @DeleteMapping("/reviews/{reviewId}")
    public ApiResponse<Void> deleteReviewByAdmin(@Parameter(hidden = true) @CurrentUser Long userId,
                                                 @PathVariable Long reviewId) {
        adminUsecase.deleteReviewByAdmin(userId, reviewId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.ADMIN_REVIEW_DELETE_SUCCESS.getMessage());
    }

    @Operation(summary = "어드민 캠페인 신청 승인")
    @PostMapping("/campaigns/{campaignId}/approval")
    public ApiResponse<Void> approveCampaign(@Parameter(hidden = true) @CurrentUser Long userId,
                                             @PathVariable Long campaignId) {
        adminUsecase.approveCampaign(userId, campaignId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.ADMIN_CAMPAIGN_APPROVAL_SUCCESS.getMessage());
    }

    @Operation(summary = "어드민 크리에이터 회원가입 승인")
    @PostMapping("/creators/{userId}/registration/approval")
    public ApiResponse<Void> approveCreator(@Parameter(hidden = true) @CurrentUser Long adminUserId,
                                            @PathVariable Long userId) {
        adminUsecase.approveCreator(adminUserId, userId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.ADMIN_CREATOR_APPROVAL_SUCCESS.getMessage());
    }
}
