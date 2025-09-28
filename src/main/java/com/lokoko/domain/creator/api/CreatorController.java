package com.lokoko.domain.creator.api;

import com.lokoko.domain.creator.api.dto.request.CreatorInfoUpdateRequest;
import com.lokoko.domain.creator.api.dto.request.CreatorMyPageUpdateRequest;
import com.lokoko.domain.creator.api.dto.request.CreatorProfileImageRequest;
import com.lokoko.domain.creator.api.dto.response.CreatorAddressInfo;
import com.lokoko.domain.creator.api.dto.response.CreatorInfoResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorMyCampaignListResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorMyPageResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorProfileImageResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorRegisterCompleteResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorSnsConnectedResponse;
import com.lokoko.domain.creator.api.message.ResponseMessage;
import com.lokoko.domain.creator.application.service.CreatorUsecase;
import com.lokoko.domain.creator.application.service.TikTokApiService;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CREATOR")
@RestController
@RequestMapping("/api/creator")
@RequiredArgsConstructor
public class CreatorController {

    private final CreatorUsecase creatorUsecase;
    private final TikTokApiService tikTokApiService;

    @Operation(summary = "크리에이터 마이페이지 조회")
    @GetMapping("/profile")
    public ApiResponse<CreatorMyPageResponse> getProfile(@Parameter(hidden = true) @CurrentUser Long userId) {

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PROFILE_FETCH_SUCCESS.getMessage(),
                creatorUsecase.getMyProfile(userId));
    }

    @Operation(summary = "크리에이터 마이페이지 내가 참여중/참여한 캠페인 목록 조회 [페이지네이션]")
    @GetMapping("/profile/campaigns")
    public ApiResponse<CreatorMyCampaignListResponse> getMyCampaigns(@Parameter(hidden = true) @CurrentUser Long userId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "12") int size
    ) {
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.MY_CAMPAIGN_FETCH_SUCCESS.getMessage(),
                creatorUsecase.getMyCampaigns(userId, page, size));
    }

    @Operation(summary = "크리에이터 마이페이지 수정")
    @PatchMapping("/profile")
    public ApiResponse<CreatorMyPageResponse> updateProfile(@Parameter(hidden = true) @CurrentUser Long userId,
                                                            @Valid @RequestBody CreatorMyPageUpdateRequest request) {

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PROFILE_UPDATE_SUCCESS.getMessage(),
                creatorUsecase.updateMyProfile(userId, request));
    }

    @PostMapping("/profile/image")
    @Operation(summary = "크리에이터 프로필 이미지 presignedUrl 발급")
    public ApiResponse<CreatorProfileImageResponse> createProfileImagePresignedUrl(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @Valid @RequestBody CreatorProfileImageRequest request) {

        CreatorProfileImageResponse response = creatorUsecase.createPresignedUrlForProfile(userId, request);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PROFILE_IMAGE_PRESIGNED_URL_SUCCESS.getMessage(),
                response);
    }


    @Operation(summary = "크리에이터 주소 정보 조회")
    @GetMapping("/profile/address")
    public ApiResponse<CreatorAddressInfo> getCreatorAddress(
            @Parameter(hidden = true) @CurrentUser Long userId
    ) {
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PROFILE_FETCH_ADDRESS_SUCCESS.getMessage(),
                creatorUsecase.getMyAddress(userId)
        );
    }

    @Operation(summary = "크리에이터 배송지 확정(배송받기)")
    @PostMapping("/profile/{campaignId}/address")
    public ApiResponse<Void> confirmAddress(@PathVariable Long campaignId,
                                            @Parameter(hidden = true) @CurrentUser Long userId) {
        creatorUsecase.confirmAddress(userId, campaignId);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.ADDRESS_CONFIRM_SUCCESS.getMessage(), null);
    }

    // 회원 가입
    @PatchMapping("/register/info")
    @Operation(summary = "회원가입시 크리에이터의 추가 정보를 입력/수정하는 API입니다")
    public ApiResponse<Void> updateCreatorRegisterInfo(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid CreatorInfoUpdateRequest request) {

        creatorUsecase.updateCreatorRegisterInfo(userId, request);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CREATOR_INFO_UPDATE_SUCCESS.getMessage());
    }


    @GetMapping("/register/sns-status")
    @Operation(summary = "크리에이터 SNS 연동 여부를 체크하는 API입니다")
    public ApiResponse<CreatorSnsConnectedResponse> getCreatorSnsConnected(
            @Parameter(hidden = true) @CurrentUser Long userId) {

        CreatorSnsConnectedResponse response = creatorUsecase.getCreatorSnsStatus(userId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CREATOR_GET_SNS_STATUS_SUCCESS.getMessage(),
                response);
    }

    @GetMapping("/register/info")
    @Operation(summary = "회원가입시 입력했던 추가 정보를 확인하는 API입니다")
    public ApiResponse<CreatorInfoResponse> getRegisterInfo(
            @Parameter(hidden = true) @CurrentUser Long userId) {

        CreatorInfoResponse response = creatorUsecase.getRegisterInfo(userId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CREATOR_GET_INFO_REGISTER_SUCCESS.getMessage(),
                response);
    }

    @PostMapping("/register/complete")
    @Operation(summary = "크리에이터 최종 가입 완료")
    public ApiResponse<CreatorRegisterCompleteResponse> completeCreatorSignup(
            @Parameter(hidden = true) @CurrentUser Long userId) {

        CreatorRegisterCompleteResponse result = creatorUsecase.completeCreatorSignup(userId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CREATOR_LOGIN_SUCCESS.getMessage(), result);
    }
}
