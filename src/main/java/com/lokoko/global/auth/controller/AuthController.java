package com.lokoko.global.auth.controller;

import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.auth.google.dto.RoleUpdateRequest;
import com.lokoko.global.auth.google.dto.RoleUpdateResponse;
import com.lokoko.global.auth.jwt.dto.JwtTokenResponse;
import com.lokoko.global.auth.jwt.dto.LoginResponse;
import com.lokoko.global.auth.jwt.service.JwtService;
import com.lokoko.global.auth.jwt.utils.CookieUtil;
import com.lokoko.global.auth.line.dto.LineLoginResponse;
import com.lokoko.global.auth.service.AuthService;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static com.lokoko.global.auth.controller.enums.ResponseMessage.*;
import static com.lokoko.global.auth.jwt.utils.JwtProvider.ACCESS_TOKEN_HEADER;
import static com.lokoko.global.auth.jwt.utils.JwtProvider.REFRESH_TOKEN_HEADER;

@Slf4j
@Tag(name = "AUTH")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final CookieUtil cookieUtil;

    @Operation(summary = "라인 소셜 로그인, 리다이렉션")
    @GetMapping("/line/redirect")
    public void redirectToLineAuth(HttpServletResponse response) throws IOException {
        String authorizeUrl = authService.generateLineLoginUrl();
        response.sendRedirect(authorizeUrl);
    }

    @Operation(summary = "라인 소셜 로그인, JWT 토큰 발급 후 저장")
    @GetMapping("/line/login")
    public ApiResponse<LineLoginResponse> lineLogin(@RequestParam("code") String code,
                                                    @RequestParam("state") String state, HttpServletResponse response) {
        LoginResponse tokens = authService.loginWithLine(code, state);
        LineLoginResponse loginResponse = LineLoginResponse.from(tokens);
        cookieUtil.setCookie(ACCESS_TOKEN_HEADER, tokens.accessToken(), response);
        cookieUtil.setCookie(REFRESH_TOKEN_HEADER, tokens.refreshToken(), response);

        return ApiResponse.success(HttpStatus.OK, LOGIN_SUCCESS.getMessage(), loginResponse);
    }

    @Operation(summary = "구글 소셜 로그인, 리다이렉션")
    @GetMapping("/google/redirect")
    public void redirectToGoogleAuth(HttpServletResponse response) throws IOException {
        String authorizeUrl = authService.generateGoogleLoginUrl();
        response.sendRedirect(authorizeUrl);
    }

    @Operation(summary = "구글 소셜 로그인, JWT 토큰 발급 후 저장")
    @GetMapping("/google/login")
    public ApiResponse<LineLoginResponse> googleLogin(@RequestParam("code") String code,
                                                      @RequestParam("state") String state,
                                                      HttpServletResponse response) {

        LoginResponse tokens = authService.loginWithGoogle(code, state);
        LineLoginResponse loginResponse = LineLoginResponse.from(tokens);
        cookieUtil.setCookie(ACCESS_TOKEN_HEADER, tokens.accessToken(), response);
        cookieUtil.setCookie(REFRESH_TOKEN_HEADER, tokens.refreshToken(), response);

        return ApiResponse.success(HttpStatus.OK, LOGIN_SUCCESS.getMessage(), loginResponse);
    }

    @Operation(summary = "사용자 역할 설정")
    @PostMapping("/role")
    public ApiResponse<RoleUpdateResponse> updateUserRole(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid RoleUpdateRequest request,
            HttpServletResponse response) {


        RoleUpdateResponse roleResponse = authService.updateUserRole(userId, request.role());

        cookieUtil.setCookie(ACCESS_TOKEN_HEADER, roleResponse.accessToken(), response);
        cookieUtil.setCookie(REFRESH_TOKEN_HEADER, roleResponse.refreshToken(), response);

        return ApiResponse.success(HttpStatus.OK, ROLE_ASSIGNED_SUCCESS.getMessage(), roleResponse);
    }

    /**
     * Todo: 테스트용으로 JWT 토큰을 발급하고, 쿠키와 헤더에 저장하는 엔드포인트, 추후 제거 예정
     */
    @Operation(summary = "테스트용 JWT 토큰 발급")
    @PostMapping("/login")
    public ApiResponse<JwtLoginResponse> login(@RequestBody @Valid TestLoginRequest request) {
        JwtTokenResponse tokenDto = jwtService.issueTokensForTest(request.userId());
        JwtLoginResponse loginResponse = JwtLoginResponse.of(tokenDto);

        return ApiResponse.success(HttpStatus.OK, LOGIN_SUCCESS.getMessage(), loginResponse);
    }

    @PostMapping("/refresh")
    @Operation(summary = "RefreshToken 재발급")
    public ApiResponse<Void> reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        JwtTokenResponse tokens = jwtService.reissueJwtToken(request);
        cookieUtil.setCookie(ACCESS_TOKEN_HEADER, tokens.accessToken(), response);
        cookieUtil.setCookie(REFRESH_TOKEN_HEADER, tokens.refreshToken(), response);

        return ApiResponse.success(HttpStatus.OK, REFRESH_TOKEN_REISSUE.getMessage());
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);

        return ApiResponse.success(HttpStatus.OK, LOGOUT_SUCCESS.getMessage());
    }
}
