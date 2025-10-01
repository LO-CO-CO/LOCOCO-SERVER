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
import com.lokoko.global.auth.jwt.service.JwtService;
import com.lokoko.global.auth.jwt.utils.CookieUtil;
import com.lokoko.global.auth.jwt.dto.GenerateTokenDto;
import com.lokoko.global.auth.jwt.dto.JwtTokenResponse;
import com.lokoko.domain.user.application.service.UserGetService;
import com.lokoko.domain.user.domain.entity.User;
import static com.lokoko.global.auth.jwt.utils.JwtProvider.ACCESS_TOKEN_HEADER;
import static com.lokoko.global.auth.jwt.utils.JwtProvider.REFRESH_TOKEN_HEADER;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Tag(name = "SNS_CONNECTION", description = "SNS(틱톡 및 인스타그램) 계정 연결 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SnsConnectionController {

    private final TikTokConnectionUsecase tikTokConnectionUsecase;
    private final InstaConnectionUsecase instaConnectionUsecase;

    private final InstaOauthClient instaOAuthClient;
    private final OAuthStateManager oAuthStateManager;
    private final JwtService jwtService;
    private final CookieUtil cookieUtil;
    private final UserGetService userGetService;

    @Operation(summary = "TikTok 계정 연동 / TikTok OAuth 인증 페이지로 리다이렉트")
    @GetMapping("/sns/tiktok/connect")
    public void connectTikTok(HttpServletResponse response,
                              @Parameter(hidden = true) @CurrentUser Long userId,
                              @RequestParam String returnTo) throws IOException {
        String authUrl = tikTokConnectionUsecase.generateConnectionUrl(userId, returnTo);
        response.sendRedirect(authUrl);
    }


    @Operation(summary = "TikTok OAuth 콜백 / 인증 후 콜백을 처리 및 계정 연결")
    @GetMapping("/tiktok/callback")
    public ApiResponse<TikTokConnectionResponse> handleTikTokCallback(@RequestParam("code") String code,
                                                                       @RequestParam("state") String state,
                                                                       HttpServletResponse response) {
        String returnTo = null;
        Long userId = null;
        try {
            userId = oAuthStateManager.validateAndGetCreatorId(state);
            returnTo = oAuthStateManager.getReturnTo(state);
            TikTokConnectionResponse connectionResponse = tikTokConnectionUsecase.connectTikTok(userId, code, returnTo);

            // JWT 토큰 생성 및 쿠키 설정
            User user = userGetService.findUserById(userId);
            String googleId = user.getGoogleId() != null ? user.getGoogleId() : "";

            GenerateTokenDto tokenDto = GenerateTokenDto.of(userId, user.getRole().name(), googleId);
            JwtTokenResponse tokens = jwtService.generateJwtToken(tokenDto);

            cookieUtil.setCookie(ACCESS_TOKEN_HEADER, tokens.accessToken(), response);
            cookieUtil.setCookie(REFRESH_TOKEN_HEADER, tokens.refreshToken(), response);

            return ApiResponse.success(HttpStatus.OK, ResponseMessage.TIKTOK_CONNECT_SUCCESS.getMessage(), connectionResponse);
        } catch (Exception e) {
            if (returnTo == null) {
                returnTo = oAuthStateManager.getReturnTo(state);
            }

            // 에러 발생 시에도 userId가 있으면 토큰 발급
            if (userId != null) {
                try {
                    User user = userGetService.findUserById(userId);
                    String googleId = user.getGoogleId() != null ? user.getGoogleId() : "";

                    GenerateTokenDto tokenDto = GenerateTokenDto.of(userId, user.getRole().name(), googleId);
                    JwtTokenResponse tokens = jwtService.generateJwtToken(tokenDto);

                    cookieUtil.setCookie(ACCESS_TOKEN_HEADER, tokens.accessToken(), response);
                    cookieUtil.setCookie(REFRESH_TOKEN_HEADER, tokens.refreshToken(), response);
                } catch (Exception tokenError) {
                    // 토큰 발급 실패는 무시하고 리다이렉트
                }
            }

            TikTokConnectionResponse errorResponse = new TikTokConnectionResponse(returnTo);
            return ApiResponse.error(HttpStatus.BAD_REQUEST, ResponseMessage.TIKTOK_CONNECT_FAIL.getMessage(), errorResponse);
        }
    }

    @Operation(summary = "Instagram 계정 연동 / Creator가 Instagram OAuth 인증 페이지로 리다이렉트")
    @GetMapping("/sns/instagram/connect")
    public void connectInstagram(HttpServletResponse response,
                                 @Parameter(hidden = true) @CurrentUser Long userId) throws IOException {
        String authUrl = instaConnectionUsecase.buildAuthorizationUrl(userId);
        response.sendRedirect(authUrl);
    }

    @Operation(summary = "Instagram OAuth 콜백 / 인증 후 code로 액세스 토큰 교환 및 계정 연결")
    @GetMapping("/sns/instagram/callback")
    public ApiResponse<InstagramConnectionResponse> handleInstagramCallback(
            @RequestParam("code") String code, @RequestParam("state") String state) {
        Long userId = oAuthStateManager.validateAndGetCreatorId(state);
        InstagramConnectionResponse response = instaConnectionUsecase.connectInstagram(userId, code);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.INSTAGRAM_CONNECT_SUCCESS.getMessage(), response);
    }
}
