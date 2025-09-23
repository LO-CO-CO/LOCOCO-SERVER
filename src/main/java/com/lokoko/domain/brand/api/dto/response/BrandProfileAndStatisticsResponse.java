package com.lokoko.domain.brand.api.dto.response;

import com.lokoko.domain.brand.domain.entity.Brand;
import io.swagger.v3.oas.annotations.media.Schema;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record BrandProfileAndStatisticsResponse(

        @Schema(requiredMode = REQUIRED, description = "브랜드 id", example = "1")
        Long brandId,
        @Schema(requiredMode = REQUIRED, description = "브랜드 이름", example = "I WANT REST")
        String brandName,
        @Schema(requiredMode = REQUIRED, description = "브랜드 이메일", example = "hello@gamil.com")
        String email,
        @Schema(requiredMode = REQUIRED, description = "브랜드 프로필 이미지 url")
        String profileImageUrl,
        @Schema(requiredMode = REQUIRED, description = "브랜드가 개설한 캠페인의 통계 정보")
        Statistics statistics
) {
    public record Statistics(
            @Schema(requiredMode = REQUIRED, description = "진행 중인 캠페인 수" , example = "1")
            Integer ongoingCampaigns,
            @Schema(requiredMode = REQUIRED, description = "종료 된 캠페인 수", example = "1")
            Integer completedCampaigns
    ){
        public static Statistics of(Integer ongoingCampaigns, Integer completedCampaigns) {
            return new Statistics(ongoingCampaigns, completedCampaigns);
        }
    }

    public static BrandProfileAndStatisticsResponse of(Brand brand, Integer ongoingNumber, Integer completedNumber) {
        return new BrandProfileAndStatisticsResponse(
                brand.getId(),
                brand.getBrandName(),
                brand.getUser().getEmail(),
                brand.getUser().getProfileImageUrl(),
                Statistics.of(ongoingNumber, completedNumber)
        );
    }
}

