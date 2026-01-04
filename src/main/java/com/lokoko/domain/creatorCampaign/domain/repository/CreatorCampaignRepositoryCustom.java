package com.lokoko.domain.creatorCampaign.domain.repository;

import com.lokoko.domain.brand.api.dto.request.ApplicantStatus;
import com.lokoko.domain.brand.api.dto.response.CampaignApplicantListResponse;
import com.lokoko.domain.user.api.dto.response.AdminCreatorListResponse;
import org.springframework.data.domain.Pageable;

public interface CreatorCampaignRepositoryCustom {

    CampaignApplicantListResponse findCampaignApplicants(Long brandId, Long campaignId, Pageable pageable, ApplicantStatus status);

    AdminCreatorListResponse findAdminCreators(Pageable pageable);

}
