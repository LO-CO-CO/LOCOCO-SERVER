package com.lokoko.domain.brand.api.dto.response;

import com.lokoko.domain.brand.domain.entity.Brand;

public record BrandProfileAndStatisticsResponse(

        Long brandId,
        String brandName,
        String email,
        String profileImageUrl,
        Statistics statistics
) {
    public record Statistics(
            Integer ongoingCampaigns,
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

