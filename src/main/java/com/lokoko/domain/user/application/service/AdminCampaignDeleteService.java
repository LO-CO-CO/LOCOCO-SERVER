package com.lokoko.domain.user.application.service;

import com.lokoko.domain.campaign.domain.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCampaignDeleteService {

    private final CampaignRepository campaignRepository;

    @Transactional
    public void deleteCampaigns(List<Long> campaignIds) {
        campaignRepository.batchSoftDeleteCampaigns(campaignIds);
    }
}
