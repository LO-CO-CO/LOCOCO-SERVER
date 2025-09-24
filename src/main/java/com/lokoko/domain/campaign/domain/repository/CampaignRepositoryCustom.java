package com.lokoko.domain.campaign.domain.repository;

import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignInfoListResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignListResponse;
import com.lokoko.domain.brand.api.dto.response.CampaignDashboard;
import com.lokoko.domain.campaign.api.dto.response.MainPageCampaignListResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageUpcomingCampaignListResponse;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductTypeFilter;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatusFilter;
import com.lokoko.domain.campaign.domain.entity.enums.LanguageFilter;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CampaignRepositoryCustom {


    MainPageCampaignListResponse findCampaignsInMainPage(Long userId, LanguageFilter lang, CampaignProductTypeFilter category, Pageable pageable);

    MainPageUpcomingCampaignListResponse findUpcomingCampaignsInMainPage(LanguageFilter lang, CampaignProductTypeFilter category);

    BrandMyCampaignInfoListResponse findSimpleCampaignInfoByBrandId(Long brandId);

    BrandMyCampaignListResponse findBrandMyCampaigns(Long brandId, CampaignStatusFilter status, Pageable pageable);

    List<CampaignDashboard> findBrandDashboardCampaigns(Long brandId, Pageable pageable);

    Long countBrandDashboardCampaigns(Long brandId);
}
