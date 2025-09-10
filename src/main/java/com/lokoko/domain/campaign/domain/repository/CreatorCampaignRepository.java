package com.lokoko.domain.campaign.domain.repository;

import com.lokoko.domain.campaign.domain.entity.CreatorCampaign;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CreatorCampaignRepository extends JpaRepository<CreatorCampaign, Long> {

    Optional<CreatorCampaign> findByCampaign_IdAndCreator_Id(Long campaignId, Long creatorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CreatorCampaign c where c.id = :id")
    CreatorCampaign getByIdForUpdate(@Param("id") Long id);
}
