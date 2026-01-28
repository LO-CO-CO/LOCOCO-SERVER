package com.lokoko.domain.campaign.domain.repository;

import com.lokoko.domain.brand.api.dto.response.BrandDashboardCampaignListResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignInfoListResponse;
import com.lokoko.domain.brand.api.dto.response.BrandMyCampaignListResponse;
import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.campaign.api.dto.response.CampaignParticipatedResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageCampaignListResponse;
import com.lokoko.domain.campaign.api.dto.response.MainPageUpcomingCampaignListResponse;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignProductTypeFilter;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatusFilter;
import com.lokoko.domain.campaign.domain.entity.enums.LanguageFilter;

import java.util.List;

import com.lokoko.domain.user.domain.entity.enums.ApprovedStatus;
import com.lokoko.domain.user.api.dto.response.AdminCampaignListResponse;
import org.springframework.data.domain.Pageable;

public interface CampaignRepositoryCustom {


    MainPageCampaignListResponse findCampaignsInMainPage(LanguageFilter lang,
                                                         CampaignProductTypeFilter category, Pageable pageable);

    MainPageUpcomingCampaignListResponse findUpcomingCampaignsInMainPage(LanguageFilter lang,
                                                                         CampaignProductTypeFilter category);

    BrandMyCampaignInfoListResponse findSimpleCampaignInfoByBrandId(Long brandId);

    BrandMyCampaignListResponse findBrandMyCampaigns(Long brandId, CampaignStatusFilter status, Pageable pageable);

    BrandDashboardCampaignListResponse findBrandDashboardCampaigns(Long brandId, Pageable pageable);

    List<CampaignParticipatedResponse> findInReviewCampaignTitlesByBrand(Brand brand);

    AdminCampaignListResponse findAllCampaignsByAdmin(ApprovedStatus status, Pageable pageable);

}
