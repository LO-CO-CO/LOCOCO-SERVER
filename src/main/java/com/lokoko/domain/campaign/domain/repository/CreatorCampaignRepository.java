package com.lokoko.domain.campaign.domain.repository;

import com.lokoko.domain.campaign.domain.entity.CreatorCampaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreatorCampaignRepository extends JpaRepository<CreatorCampaign, Long> {
    Optional<CreatorCampaign> findByCreatorIdAndCampaignId(Long creatorId, Long campaignId);
}
