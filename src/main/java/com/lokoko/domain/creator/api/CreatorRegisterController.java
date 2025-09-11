package com.lokoko.domain.creator.api;

import com.lokoko.domain.creator.api.dto.request.CreatorIdCheckRequest;
import com.lokoko.domain.creator.api.dto.request.CreatorInfoUpdateRequest;
import com.lokoko.domain.creator.api.dto.response.CreatorInfoResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorRegisterCompleteResponse;
import com.lokoko.domain.creator.api.dto.response.CreatorSnsConnectedResponse;
import com.lokoko.domain.creator.api.message.ResponseMessage;
import com.lokoko.domain.creator.application.CreatorRegisterService;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CREATOR")
@RestController
@RequestMapping("/api/creator")
@RequiredArgsConstructor
public class CreatorRegisterController {
    private final CreatorRegisterService creatorRegisterService;

    @PostMapping("/id")
    @Operation(summary = "크리에이터 ID 사용 가능 여부를 체크하는 API입니다")
    public ApiResponse<Void> checkCreatorId(@RequestBody @Valid CreatorIdCheckRequest request,
                                            @Parameter(hidden = true) @CurrentUser Long userId) {

        creatorRegisterService.validateCreatorId(request.creatorName(), userId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CREATOR_ID_CHECK_SUCCESS.getMessage());
    }

    @Operation(summary = "회원가입시 크리에이터의 추가 정보를 입력/수정하는 API 입니다.")
    @PatchMapping("/info")
    public ApiResponse<Void> updateCreatorInfo(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid CreatorInfoUpdateRequest request) {

        creatorRegisterService.updateCreatorInfo(userId, request);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CREATOR_INFO_UPDATE_SUCCESS.getMessage()
        );
    }

    @GetMapping("/sns")
    @Operation(summary = "크리에이터 SNS 연동 여부를 체크하는 API입니다")
    public ApiResponse<CreatorSnsConnectedResponse> checkCreatorSnsConnected(@Parameter(hidden = true) @CurrentUser Long userId) {

        CreatorSnsConnectedResponse response = creatorRegisterService.validateCreatorSnsConnected(userId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.CREATOR_SNS_STATE_SUCCESS.getMessage(), response);
    }

    @GetMapping("register/info")
    @Operation(summary = "회원가입시 입력했던 추가 정보를 확인하는 API입니다. (회원가입시 수정을 할 때 사용함)")

    public ApiResponse<CreatorInfoResponse> getCreatorInfo(
            @Parameter(hidden = true) @CurrentUser Long userId) {

        CreatorInfoResponse response = creatorRegisterService.getCreatorInfo(userId);

        return ApiResponse.success(HttpStatus.OK,
                ResponseMessage.CREATOR_GET_INFO_REGISTER_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "크리에이터 최종 가입 완료")
    @PostMapping("/complete")
    public ApiResponse<CreatorRegisterCompleteResponse> completeCreatorSignup(
            @Parameter(hidden = true) @CurrentUser Long userId,
            HttpServletResponse response) {

        CreatorRegisterCompleteResponse result = creatorRegisterService.completeCreatorSignup(userId);

        return ApiResponse.success(HttpStatus.OK,
                ResponseMessage.CREATOR_LOGIN_SUCCESS.getMessage(), result);
    }
}


