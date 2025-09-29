package com.lokoko.domain.creatorCampaign.domain.repository;


import com.lokoko.domain.campaign.domain.entity.Campaign;
import com.lokoko.domain.campaign.domain.entity.enums.CampaignStatus;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.creatorCampaign.domain.enums.ParticipationStatus;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CreatorCampaignRepository extends JpaRepository<CreatorCampaign, Long>,
        CreatorCampaignRepositoryCustom {

    Optional<CreatorCampaign> findByCampaignIdAndCreatorId(Long campaignId, Long creatorId);

    boolean existsByCampaignIdAndCreatorId(Long campaignId, Long creatorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CreatorCampaign c where c.id = :id")
    CreatorCampaign getByIdForUpdate(@Param("id") Long id);

    Optional<CreatorCampaign> findByCreatorIdAndCampaignId(Long creatorId, Long campaignId);

    @Query("""
                select cc
                from CreatorCampaign cc
                join cc.campaign c
                where cc.creator.id = :creatorId
                  and c.id = :campaignId
                  and c.campaignStatus = :campaignStatus
                  and cc.status in :allowedStatuses
            """)
    Optional<CreatorCampaign> findReviewableInReviewByCampaign(
            @Param("creatorId") Long creatorId,
            @Param("campaignId") Long campaignId,
            @Param("campaignStatus") CampaignStatus campaignStatus,
            @Param("allowedStatuses") Collection<ParticipationStatus> allowedStatuses
    );

    @Query("""
                select cc
                from CreatorCampaign cc
                  join fetch cc.campaign c
                where cc.creator.id = :creatorId
                order by cc.appliedAt desc, cc.id desc
            """)
    Slice<CreatorCampaign> findSliceWithCampaignByCreator(Long creatorId, Pageable pageable);

    @Query("""
                select count(cc)
                from CreatorCampaign cc
                where cc.creator.id = :creatorId
            """)
    Long countByCreatorId(Long creatorId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE CreatorCampaign cc SET cc.status = 'APPROVED' WHERE cc.id IN :applicationIds")
    int bulkApproveApplicationStatus(List<Long> applicationIds);


    @Query("SELECT cc.id FROM CreatorCampaign cc WHERE cc.id IN :applicationIds AND cc.status = 'PENDING' " +
            "AND cc.campaign.id = :campaignId")
    List<Long> findPendingApplicationIds(Long campaignId, List<Long> applicationIds);

    Optional<CreatorCampaign> findByCampaignAndCreator_Id(Campaign campaign, Long creatorId);

    @Query("""
                select cc
                from CreatorCampaign cc
                join fetch cc.campaign c
                where cc.creator.id = :creatorId
                  and c.campaignStatus = :campaignStatus
                  and cc.status in :statuses
                order by cc.id desc
            """)
    List<CreatorCampaign> findReviewablesInReview(@Param("creatorId") Long creatorId,
                                                  @Param("campaignStatus") CampaignStatus campaignStatus,
                                                  @Param("statuses") Collection<ParticipationStatus> statuses);
}
