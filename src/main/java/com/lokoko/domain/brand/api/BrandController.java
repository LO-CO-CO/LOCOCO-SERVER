package com.lokoko.domain.brand.api;

import com.lokoko.domain.brand.api.dto.request.BrandInfoUpdateRequest;
import com.lokoko.domain.brand.api.message.ResponseMessage;
import com.lokoko.domain.brand.application.BrandService;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    @PatchMapping("/register/info")
    @Operation(summary = "회원가입시 브랜드 추가 정보를 입력하는 API 입니다.")

    public ApiResponse<Void> updateBrandInfo(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid BrandInfoUpdateRequest request) {

        brandService.updateBrandInfo(userId, request);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.BRAND_INFO_UPDATE_SUCCESS.getMessage());
    }
}