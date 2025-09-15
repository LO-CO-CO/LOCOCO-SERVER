package com.lokoko.domain.creatorCampaign.domain.repository;


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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CreatorCampaignRepository extends JpaRepository<CreatorCampaign, Long> {

    Optional<CreatorCampaign> findByCampaignIdAndCreatorId(Long campaignId, Long creatorId);

    boolean existsByCampaignIdAndCreatorId(Long campaignId, Long creatorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CreatorCampaign c where c.id = :id")
    CreatorCampaign getByIdForUpdate(@Param("id") Long id);

    Optional<CreatorCampaign> findByCreatorIdAndCampaignId(Long creatorId, Long campaignId);

    @Query("""
            select cc
            from CreatorCampaign cc
            join fetch cc.campaign c
            where cc.creator.id = :creatorId
              and cc.status in :statuses
            order by cc.appliedAt desc
            """)
    List<CreatorCampaign> findAllByCreatorAndStatuses(Long creatorId, Collection<ParticipationStatus> statuses);

    @Query("""
                select cc
                from CreatorCampaign cc
                  join fetch cc.campaign c
                where cc.creator.id = :creatorId
                order by cc.appliedAt desc
            """)
    Slice<CreatorCampaign> findSliceWithCampaignByCreator(Long creatorId, Pageable pageable);
}
