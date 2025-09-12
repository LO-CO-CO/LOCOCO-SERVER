package com.lokoko.domain.creator.api;

import com.lokoko.domain.creator.api.dto.request.CreatorIdCheckRequest;
import com.lokoko.domain.creator.api.dto.request.CreatorInfoUpdateRequest;
import com.lokoko.domain.creator.api.dto.request.CreatorMyPageUpdateRequest;
import com.lokoko.domain.creator.api.dto.response.CreatorInfoResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorMyPageResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorRegisterCompleteResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorSnsConnectedResponse;
import com.lokoko.domain.creator.api.message.ResponseMessage;
import com.lokoko.domain.creator.application.service.CreatorUsecase;
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
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CREATOR")
@RestController
@RequestMapping("/api/creator")
@RequiredArgsConstructor
public class CreatorController {

    private final CreatorUsecase creatorUsecase;

    @Operation(summary = "크리에이터 마이페이지 조회")
    @GetMapping("/profile")
    public ApiResponse<CreatorMyPageResponse> getProfile(@Parameter(hidden = true) @CurrentUser Long userId) {

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PROFILE_FETCH_SUCCESS.getMessage(),
                creatorUsecase.getMyProfile(userId));
    }

    @Operation(summary = "크리에이터 마이페이지 수정")
    @PatchMapping("/profile")
    public ApiResponse<CreatorMyPageResponse> updateProfile(@Parameter(hidden = true) @CurrentUser Long userId,
                                                            @Valid @RequestBody CreatorMyPageUpdateRequest request) {

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.PROFILE_UPDATE_SUCCESS.getMessage(),
                creatorUsecase.updateMyProfile(userId, request));
    }

    @Operation(summary = "크리에이터 배송지 확정(배송받기)")
    @PostMapping("/{campaignId}/address")
    public ApiResponse<Void> confirmAddress(@PathVariable Long campaignId,
                                            @Parameter(hidden = true) @CurrentUser Long userId) {
        creatorUsecase.confirmAddress(userId, campaignId);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.ADDRESS_CONFIRM_SUCCESS.getMessage(), null);
    }

    // 회원 가입
    @PostMapping("/register/check-id")
    @Operation(summary = "크리에이터 ID 사용 가능 여부를 체크하는 API입니다")
    public ApiResponse<Void> checkCreatorId(
            @RequestBody @Valid CreatorIdCheckRequest request,
            @Parameter(hidden = true) @CurrentUser Long userId) {

        creatorUsecase.checkCreatorIdAvailable(request.creatorName(), userId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CREATOR_ID_CHECK_SUCCESS.getMessage());
    }

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
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CREATOR_GET_SNS_STATUS_SUCCESS.getMessage(), response);
    }

    @GetMapping("/register/info")
    @Operation(summary = "회원가입시 입력했던 추가 정보를 확인하는 API입니다")
    public ApiResponse<CreatorInfoResponse> getRegisterInfo(
            @Parameter(hidden = true) @CurrentUser Long userId) {

        CreatorInfoResponse response = creatorUsecase.getRegisterInfo(userId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CREATOR_GET_INFO_REGISTER_SUCCESS.getMessage(), response);
    }

    @PostMapping("/register/complete")
    @Operation(summary = "크리에이터 최종 가입 완료")
    public ApiResponse<CreatorRegisterCompleteResponse> completeCreatorSignup(
            @Parameter(hidden = true) @CurrentUser Long userId) {

        CreatorRegisterCompleteResponse result = creatorUsecase.completeCreatorSignup(userId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CREATOR_LOGIN_SUCCESS.getMessage(), result);
    }
}
