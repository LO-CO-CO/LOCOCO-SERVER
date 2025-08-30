package com.lokoko.domain.campagin.domain.repository;

import com.lokoko.domain.campagin.domain.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
}
