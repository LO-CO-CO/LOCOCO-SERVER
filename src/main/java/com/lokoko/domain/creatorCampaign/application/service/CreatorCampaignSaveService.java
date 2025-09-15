package com.lokoko.domain.creatorCampaign.application.service;

import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.repository.CreatorCampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatorCampaignSaveService {

    private final CreatorCampaignRepository creatorCampaignRepository;

    @Transactional
    public CreatorCampaign save(CreatorCampaign creatorCampaign) {
        return creatorCampaignRepository.save(creatorCampaign);
    }
}
