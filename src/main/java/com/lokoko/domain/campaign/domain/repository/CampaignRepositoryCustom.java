package com.lokoko.domain.campaign.domain.repository;

import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignInfoListResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageCampaignListResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageUpcomingCampaignListResponse;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductTypeFilter;
import com.lokoko.domain.campaign.domain.entity.enums.LanguageFilter;
import org.springframework.data.domain.Pageable;

public interface CampaignRepositoryCustom {


    MainPageCampaignListResponse findCampaignsInMainPage(Long userId, LanguageFilter lang, CampaignProductTypeFilter category, Pageable pageable);

    MainPageUpcomingCampaignListResponse findUpcomingCampaignsInMainPage(LanguageFilter lang, CampaignProductTypeFilter category);

    BrandMyCampaignInfoListResponse findSimpleCampaignInfoByBrandId(Long brandId);
}
