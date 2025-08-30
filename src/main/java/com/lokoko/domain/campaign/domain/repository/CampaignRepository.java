package com.lokoko.domain.campaign.domain.repository;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
}
