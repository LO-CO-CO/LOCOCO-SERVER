package com.lokoko.domain.brand.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BrandMyPageUpdateRequest(
        @Schema(description = "프로필 이미지 URL")
        String profileImageUrl,

        @Size(max = 15, message = "브랜드명은 최대 15자까지 가능합니다")
        String brandName,

        @Size(max = 10, message = "담당자명은 최대 10자까지 가능합니다")
        String managerName,

        @Size(max = 10, message = "담당자 연락처는 최대 10자까지 가능합니다")
        @Pattern(regexp = "^[0-9]+$", message = "연락처는 숫자만 입력 가능합니다")
        String phoneNumber,

        @Size(max = 30, message = "도로명 주소는 최대 30자까지 가능합니다")
        String roadAddress,

        @Size(max = 30, message = "상세 주소는 최대 30자까지 가능합니다")
        String addressDetail
) {
}