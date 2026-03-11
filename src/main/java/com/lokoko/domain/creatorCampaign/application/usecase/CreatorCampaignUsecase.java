package com.lokoko.domain.creatorCampaign.application.usecase;

import com.lokoko.domain.campaign.application.service.CampaignGetService;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.creator.application.service.CreatorGetService;
import com.lokoko.domain.creator.domain.entity.Creator;
import com.lokoko.domain.creatorCampaign.application.mapper.CreatorCampaignMapper;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignGetService;
import com.lokoko.domain.creatorCampaign.application.service.CreatorCampaignSaveService;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatorCampaignUsecase {

    private final CreatorGetService creatorGetService;
    private final CampaignGetService campaignGetService;
    private final CreatorCampaignGetService creatorCampaignGetService;

    private final CreatorCampaignSaveService creatorCampaignSaveService;

    private final CreatorCampaignMapper creatorCampaignMapper;

    @Transactional
    public void participateCampaign(Long userId, Long campaignId) {
        Creator creator = creatorGetService.findByUserId(userId);
        Campaign campaign = campaignGetService.findByCampaignId(campaignId);

        Instant now = Instant.now();

        campaign.validateParticipatableAt(now);
        creatorCampaignGetService.findExistingParticipation(campaignId, creator.getId());

        CreatorCampaign creatorCampaign = creatorCampaignMapper.toCampaignParticipation(creator, campaign, now);

        creatorCampaignSaveService.save(creatorCampaign);

        campaign.increaseApplicant();
    }
}
