package com.lokoko.domain.campaign.domain.repository;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    /**
     * Campaign 과 brand 를 fetch join
     */
    @EntityGraph(attributePaths = {"brand"})
    @Query("SELECT c FROM Campaign c WHERE c.id = :id")
    Optional<Campaign> findCampaignWithBrandById(Long id);
}
