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
public interface CreatorCampaignRepository extends JpaRepository<CreatorCampaign, Long>, CreatorCampaignQRepository {

    @Query("""
                select creatorCampaign
                from CreatorCampaign creatorCampaign
                where creatorCampaign.creator.id  = :creatorId
                  and creatorCampaign.campaign.id = :campaignId
            """)
    Optional<CreatorCampaign> findByCreatorIdAndCampaignId(@Param("creatorId") Long creatorId,
                                                           @Param("campaignId") Long campaignId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CreatorCampaign c where c.id = :id")
    CreatorCampaign getByIdForUpdate(@Param("id") Long id);
}
