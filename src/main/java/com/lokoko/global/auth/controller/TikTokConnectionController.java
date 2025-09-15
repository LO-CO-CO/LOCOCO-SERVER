package com.lokoko.global.auth.controller;

import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.auth.controller.enums.ResponseMessage;
import com.lokoko.global.auth.service.OAuthStateManager;
import com.lokoko.global.auth.tiktok.dto.TikTokConnectionResponse;
import com.lokoko.global.auth.tiktok.service.TikTokConnectionService;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@Tag(name = "TIKTOK_CONNECTION", description = "TikTok 계정 연결 API")
@RestController
@RequestMapping("/api/auth/tiktok")
@RequiredArgsConstructor
public class TikTokConnectionController {
    
    private final TikTokConnectionService tikTokConnectionService;
    private final OAuthStateManager oAuthStateManager;

    @Operation(summary = "TikTok 계정 연결", description = "Creator가 TikTok 계정 연결 / TikTok OAuth 인증 페이지로 리다이렉트")
    @GetMapping("/connect")
    public void connectTikTok(
            @Parameter(hidden = true) @CurrentUser Long userId,
            HttpServletResponse response) throws IOException{

        String authUrl = tikTokConnectionService.generateConnectionUrl(userId);
        response.sendRedirect(authUrl);
    }

    @Operation(summary = "TikTok OAuth 콜백", description = "TikTok OAuth 인증 후 콜백을 처리 및 계정 연결 완료")
    @GetMapping("/callback")
    public ApiResponse<TikTokConnectionResponse> handleTikTokCallback(
            @RequestParam("code") String code, @RequestParam("state") String state) {

            Long userId = oAuthStateManager.validateAndGetCreatorId(state);

            TikTokConnectionResponse response = tikTokConnectionService.connectTikTok(userId, code);

            return ApiResponse.success(HttpStatus.OK, ResponseMessage.TIKTOK_CONNECT_SUCCESS.getMessage(), response);
    }
}