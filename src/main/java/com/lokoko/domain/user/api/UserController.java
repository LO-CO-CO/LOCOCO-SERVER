package com.lokoko.domain.user.api;

import com.lokoko.domain.user.api.dto.request.UserIdCheckRequest;
import com.lokoko.domain.user.api.message.ResponseMessage;
import com.lokoko.domain.user.application.service.UserService;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.auth.provider.google.dto.response.AfterLoginUserNameResponse;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.lokoko.global.auth.controller.enums.ResponseMessage.GET_LOGIN_USER_ID_SUCCESS;

@Tag(name = "USER")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 ID 중복 체크 (Customer/Creator 공통)")
    @GetMapping("/check-id")
    public ApiResponse<Void> checkUserId(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @Validated UserIdCheckRequest request) {

        userService.checkUserIdAvailable(request.userId(), userId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.USER_ID_CHECK_SUCCESS.getMessage());
    }

    @GetMapping("/name")
    @Operation(summary = "로그인한 사용자의 이름을 표시하는 API입니다.")
    public ApiResponse<AfterLoginUserNameResponse> getUserDisplayName(
            @Parameter(hidden = true) @CurrentUser Long userId) {

        AfterLoginUserNameResponse response = userService.getUserName(userId);
        return ApiResponse.success(HttpStatus.OK, GET_LOGIN_USER_ID_SUCCESS.getMessage(), response);
    }
}