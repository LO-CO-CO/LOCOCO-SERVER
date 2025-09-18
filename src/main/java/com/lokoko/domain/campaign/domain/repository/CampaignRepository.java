package com.lokoko.domain.campaign.domain.repository;

import com.lokoko.domain.campaign.domain.entity.Campaign;

import java.time.Instant;
import java.util.Optional;

import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> , CampaignRepositoryCustom {

    /**
     * Campaign 과 brand 를 fetch join
     */
    @EntityGraph(attributePaths = {"brand"})
    @Query("SELECT c FROM Campaign c WHERE c.id = :id")
    Optional<Campaign> findCampaignWithBrandById(Long id);

    @EntityGraph(attributePaths = {"brand"})
    @Query("SELECT c FROM Campaign c WHERE c.id = :id AND c.campaignStatus = :status")
    Optional<Campaign> findDraftCampaignById(Long id, @Param("status") CampaignStatus status);

    @Query("SELECT count(c) FROM Campaign c " +
            "WHERE c.brand.id = :brandId " +
            "AND c.campaignStatus NOT IN ('DRAFT', 'WAITING_APPROVAL') " +
            "AND c.applyStartDate <= :now " +
            "AND c.reviewSubmissionDeadline > :now")
    Integer countOngoingCampaignsById(@Param("brandId") Long brandId, @Param("now") Instant now);


    @Query("SELECT count(c) FROM Campaign c " +
            "WHERE c.brand.id = :brandId " +
            "AND c.campaignStatus NOT IN ('DRAFT', 'WAITING_APPROVAL') " +
            "AND c.reviewSubmissionDeadline <= :now")
    Integer countCompletedCampaignsById(@Param("brandId") Long brandId, @Param("now") Instant now);

}
