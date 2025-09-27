package com.lokoko.domain.brand.api.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BrandInfoUpdateRequest(
        @NotBlank(message = "브랜드명은 필수입니다")
        @Size(max = 15, message = "브랜드명은 최대 15자까지 가능합니다")
        String brandName,

        @NotBlank(message = "담당자명은 필수입니다")
        @Size(max = 10, message = "담당자명은 최대 10자까지 가능합니다")
        String managerName,

        @NotBlank(message = "담당자 직급은 필수입니다")
        @Size(max = 10, message = "담당자 직급은 최대 10자까지 가능합니다")
        String managerPosition,

        @NotBlank(message = "담당자 연락처는 필수입니다")
        @Size(max = 11, message = "담당자 연락처는 최대 10자까지 가능합니다")
        @Pattern(regexp = "^[0-9]+$", message = "연락처는 숫자만 입력 가능합니다")
        String phoneNumber,

        @NotBlank(message = "도로명 주소는 필수입니다")
        @Size(max = 30, message = "도로명 주소는 최대 30자까지 가능합니다")
        String roadAddress,

        @NotBlank(message = "상세 주소는 필수입니다")
        @Size(max = 30, message = "상세 주소는 최대 30자까지 가능합니다")
        String addressDetail
) {
}