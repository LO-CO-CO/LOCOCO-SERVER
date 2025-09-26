package com.lokoko.global.auth.provider.controller;

import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.auth.provider.controller.enums.ResponseMessage;
import com.lokoko.global.auth.provider.insta.config.InstaOauthClient;
import com.lokoko.global.auth.provider.insta.dto.InstagramConnectionResponse;
import com.lokoko.global.auth.provider.insta.usecase.InstaConnectionUsecase;
import com.lokoko.global.auth.provider.tiktok.dto.TikTokConnectionResponse;
import com.lokoko.global.auth.provider.tiktok.usecase.TikTokConnectionUsecase;
import com.lokoko.global.auth.service.OAuthStateManager;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SNS_CONNECTION", description = "SNS(틱톡 및 인스타그램) 계정 연결 API")
@RestController
@RequestMapping("/api/auth/sns")
@RequiredArgsConstructor
public class SnsConnectionController {

    private final TikTokConnectionUsecase tikTokConnectionUsecase;
    private final InstaConnectionUsecase instaConnectionUsecase;

    private final InstaOauthClient instaOAuthClient;
    private final OAuthStateManager oAuthStateManager;

    @Operation(summary = "TikTok 계정 연동 / TikTok OAuth 인증 페이지로 리다이렉트")
    @GetMapping("/tiktok/connect")
    public ApiResponse<String> connectTikTok(
            @Parameter(hidden = true) @CurrentUser Long userId) {
        String authUrl = tikTokConnectionUsecase.generateConnectionUrl(userId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.TIKTOK_REDIRECT_URI_GET_SUCCESS.getMessage(),
                authUrl);
    }

    @Operation(summary = "TikTok OAuth 콜백 / 인증 후 콜백을 처리 및 계정 연결")
    @GetMapping("/tiktok/callback")
    public ApiResponse<TikTokConnectionResponse> handleTikTokCallback(@RequestParam("code") String code,
                                                                      @RequestParam("state") String state) {
        Long userId = oAuthStateManager.validateAndGetCreatorId(state);
        TikTokConnectionResponse response = tikTokConnectionUsecase.connectTikTok(userId, code);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.TIKTOK_CONNECT_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "Instagram 계정 연동 / Creator가 Instagram OAuth 인증 페이지로 리다이렉트")
    @GetMapping("/instagram/connect")
    public ApiResponse<String> connectInstagram(@Parameter(hidden = true) @CurrentUser Long userId) {
        String authUrl = instaConnectionUsecase.buildAuthorizationUrl(userId);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.INSTAGRAM_REDIRECT_URI_GET_SUCCESS.getMessage(),
                authUrl);
    }

    @Operation(summary = "Instagram OAuth 콜백 / 인증 후 code로 액세스 토큰 교환 및 계정 연결")
    @GetMapping("/instagram/callback")
    public ApiResponse<InstagramConnectionResponse> handleInstagramCallback(
            @RequestParam("code") String code, @RequestParam("state") String state) {
        Long userId = oAuthStateManager.validateAndGetCreatorId(state);
        InstagramConnectionResponse response = instaConnectionUsecase.connectInstagram(userId, code);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.INSTAGRAM_CONNECT_SUCCESS.getMessage(), response);
    }
}
