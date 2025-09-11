package com.lokoko.domain.creator.api;

import com.lokoko.domain.creator.api.dto.request.CreatorMyPageUpdateRequest;
import com.lokoko.domain.creator.api.dto.response.CreatorMyPageResponse;
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
}
