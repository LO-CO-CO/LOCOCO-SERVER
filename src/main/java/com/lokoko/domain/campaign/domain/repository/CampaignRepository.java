package com.lokoko.domain.campaign.domain.repository;

import com.lokoko.domain.campaign.domain.entity.Campaign;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> , CampaignRepositoryCustom {

    /**
     * Campaign 과 brand 를 fetch join
     */
    @EntityGraph(attributePaths = {"brand"})
    @Query("SELECT c FROM Campaign c WHERE c.id = :id")
    Optional<Campaign> findCampaignWithBrandById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Campaign c WHERE c.id = :id")
    Optional<Campaign> findCampaignWithLockById(Long id);
}
