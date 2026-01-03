package com.lokoko.domain.user.api;

import com.lokoko.domain.campaign.api.dto.response.CampaignBasicResponse;
import com.lokoko.domain.user.api.dto.request.ApproveCampaignIdsRequest;
import com.lokoko.domain.user.api.dto.request.ApprovedStatus;
import com.lokoko.domain.user.api.dto.request.CampaignModifyRequest;
import com.lokoko.domain.user.api.dto.request.DeleteCampaignIdsRequest;
import com.lokoko.domain.user.api.dto.response.AdminCampaignListResponse;
import com.lokoko.domain.user.api.message.ResponseMessage;
import com.lokoko.domain.user.application.usecase.AdminUsecase;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ADMIN")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminUsecase adminUsecase;

    @Hidden
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

    @Hidden
    @Operation(summary = "어드민 크리에이터 회원가입 승인")
    @PostMapping("/creators/{userId}/registration/approval")
    public ApiResponse<Void> approveCreator(@Parameter(hidden = true) @CurrentUser Long adminUserId,
                                            @PathVariable Long userId) {
        adminUsecase.approveCreator(adminUserId, userId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.ADMIN_CREATOR_APPROVAL_SUCCESS.getMessage());
    }

    @Operation(summary = "어드민 캠페인 신청 승인 - 복수 승인 가능")
    @PostMapping("/campaigns/approval")
    public ApiResponse<Void> approveCampaigns(@Parameter(hidden = true) @CurrentUser Long userId,
                                              @RequestBody ApproveCampaignIdsRequest request) {
        adminUsecase.approveCampaigns(userId, request);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.ADMIN_CAMPAIGN_APPROVAL_SUCCESS.getMessage());
    }

    @Operation(summary = "어드민 캠페인 삭제(soft delete) - 복수 삭제 가능")
    @DeleteMapping("/campaigns")
    public ApiResponse<Void> deleteCampaigns(@Parameter(hidden = true) @CurrentUser Long userId,
                                             @RequestBody DeleteCampaignIdsRequest request){
        adminUsecase.deleteCampaigns(userId, request);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.ADMIN_CAMPAIGN_DELETE_SUCCESS.getMessage());
    }

    @Operation(summary = "어드민 - 전체 캠페인 리스트 조회(페이지네이션)")
    @GetMapping("/campaigns")
    public ApiResponse<AdminCampaignListResponse> getAllCampaigns(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestParam(required = false) ApprovedStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        AdminCampaignListResponse response = adminUsecase.findAllCampaigns(userId, status, page, size);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.ADMIN_CAMPAIGN_LIST_GET_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "발행한 캠페인 정보 단건 조회(수정 페이지 진입 시 정보 반환 용)")
    @GetMapping("/campaigns/{campaignId}")
    public ApiResponse<CampaignBasicResponse> getCampaignDetail(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable Long campaignId
    ){
        CampaignBasicResponse response = adminUsecase.findCampaignDetail(userId, campaignId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.ADMIN_CAMPAIGN_DETAIL_GET_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "어드민 - 캠페인 수정")
    @PatchMapping("/campaigns/{campaignId}")
    public ApiResponse<Void> modifyCampaign(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable Long campaignId,
            @RequestBody @Valid CampaignModifyRequest request
    ){
        adminUsecase.modifyCampaign(userId, campaignId, request);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.ADMIN_CAMPAIGN_DETAIL_GET_SUCCESS.getMessage());
    }
}
