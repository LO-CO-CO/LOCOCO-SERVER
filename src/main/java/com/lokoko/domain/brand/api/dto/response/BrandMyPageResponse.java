package com.lokoko.domain.brand.api.dto.response;

import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.user.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record BrandMyPageResponse(
        @Schema(description = "프로필 이미지 URL")
        String profileImageUrl,

        @Schema(requiredMode = REQUIRED, description = "담당자 이름")
        String managerName,

        @Schema(requiredMode = REQUIRED, description = "브랜드 이메일 (구글 로그인 이메일)")
        String email,

        @Schema(requiredMode = REQUIRED, description = "전화번호")
        String phoneNumber,

        @Schema(requiredMode = REQUIRED, description = "도로명 주소")
        String roadAddress,

        @Schema(requiredMode = REQUIRED, description = "상세 주소")
        String addressDetail
) {
    public static BrandMyPageResponse from(Brand brand, User user) {
        return new BrandMyPageResponse(
                user.getProfileImageUrl(),
                brand.getManagerName(),
                user.getEmail(),
                brand.getPhoneNumber(),
                brand.getRoadAddress(),
                brand.getAddressDetail()
        );
    }
}