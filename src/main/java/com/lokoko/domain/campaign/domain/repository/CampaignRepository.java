package com.lokoko.domain.campaign.domain.repository;

import com.lokoko.domain.brand.domain.entity.Brand;
import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long>, CampaignRepositoryCustom {

    /**
     * Campaign 과 brand 를 fetch join
     */
    @EntityGraph(attributePaths = {"brand"})
    @Query("SELECT c FROM Campaign c WHERE c.id = :id")
    Optional<Campaign> findCampaignWithBrandById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Campaign c WHERE c.id = :id")
    Optional<Campaign> findCampaignWithLockById(Long id);

    @EntityGraph(attributePaths = {"brand"})
    @Query("SELECT c FROM Campaign c WHERE c.id = :id AND c.campaignStatus = :status")
    Optional<Campaign> findDraftCampaignById(Long id, @Param("status") CampaignStatus status);

    @EntityGraph(attributePaths = {"brand"})
    @Query("SELECT c FROM Campaign c WHERE c.id = :id AND c.campaignStatus = :status")
    Optional<Campaign> findWaitingApprovalCampaignById(Long id, @Param("status") CampaignStatus campaignStatus);

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

    List<Campaign> findAllByBrandAndCampaignStatusOrderByTitleAsc(Brand brand, CampaignStatus status);


    @Modifying(clearAutomatically = true)
    @Query("UPDATE Campaign c SET c.campaignStatus = 'OPEN_RESERVED' WHERE c.id in :campaignIds")
    void batchUpdateStatusToApproved(List<Long> campaignIds);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Campaign c SET c.deleted = 1 WHERE c.id in :campaignIds")
    void batchSoftDeleteCampaigns(List<Long> campaignIds);
}
