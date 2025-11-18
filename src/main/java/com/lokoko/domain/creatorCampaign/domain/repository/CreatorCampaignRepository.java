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

    /**
     * 캠페인 ID와 참여 상태로 크리에이터 캠페인 조회
     * @param campaignId 캠페인 ID
     * @param status 참여 상태
     * @return 해당하는 크리에이터 캠페인 목록
     */
    @Query("SELECT cc FROM CreatorCampaign cc WHERE cc.campaign.id = :campaignId AND cc.status = :status")
    List<CreatorCampaign> findByCampaignIdAndStatus(@Param("campaignId") Long campaignId,
                                                     @Param("status") ParticipationStatus status);

    List<CreatorCampaign> findAllByCampaign(Campaign campaign);

    /**
     * 특정 캠페인의 특정 상태를 다른 상태로 일괄 변경
     * @param campaignId 캠페인 ID
     * @param fromStatus 변경 전 상태
     * @param toStatus 변경 후 상태
     * @return 업데이트된 레코드 수
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE CreatorCampaign cc SET cc.status = :toStatus WHERE cc.campaign.id = :campaignId AND cc.status = :fromStatus")
    int bulkUpdateStatus(@Param("campaignId") Long campaignId,
                         @Param("fromStatus") ParticipationStatus fromStatus,
                         @Param("toStatus") ParticipationStatus toStatus);

    /**
     * 특정 ID 목록의 크리에이터 캠페인 상태를 일괄 변경
     * @param ids 크리에이터 캠페인 ID 목록
     * @param toStatus 변경 후 상태
     * @return 업데이트된 레코드 수
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE CreatorCampaign cc SET cc.status = :toStatus WHERE cc.id IN :ids")
    int bulkUpdateStatusByIds(@Param("ids") List<Long> ids,
                              @Param("toStatus") ParticipationStatus toStatus);

    /**
     * 베타버전 전용: 여러 캠페인 상태에서 리뷰 작성 가능한 CreatorCampaign 조회
     * RECRUITING, RECRUITMENT_CLOSED, IN_REVIEW 상태 모두 허용
     *
     * @param creatorId 크리에이터 ID
     * @param campaignId 캠페인 ID
     * @param campaignStatuses 허용할 캠페인 상태 목록
     * @param allowedStatuses 허용할 참여 상태 목록
     * @return 리뷰 작성 가능한 CreatorCampaign
     */
    @Query("""
                select cc
                from CreatorCampaign cc
                join cc.campaign c
                where cc.creator.id = :creatorId
                  and c.id = :campaignId
                  and c.campaignStatus in :campaignStatuses
                  and cc.status in :allowedStatuses
                  and cc.addressConfirmed = true
            """)
    Optional<CreatorCampaign> findReviewableInMultipleStatusesForBeta(
            @Param("creatorId") Long creatorId,
            @Param("campaignId") Long campaignId,
            @Param("campaignStatuses") Collection<CampaignStatus> campaignStatuses,
            @Param("allowedStatuses") Collection<ParticipationStatus> allowedStatuses
    );
}
