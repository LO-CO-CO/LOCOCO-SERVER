package com.lokoko.domain.campaignReview.domain.repository;

import com.lokoko.domain.campaignReview.domain.entity.CampaignReview;
import com.lokoko.domain.campaignReview.domain.entity.enums.ReviewRound;
import com.lokoko.domain.creatorCampaign.domain.entity.CreatorCampaign;
import com.lokoko.domain.media.socialclip.domain.entity.enums.ContentType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignReviewRepository extends JpaRepository<CampaignReview, Long> {

    boolean existsByCreatorCampaignIdAndReviewRound(Long creatorCampaignId, ReviewRound reviewRound);

    boolean existsByCreatorCampaignIdAndReviewRoundAndContentType(Long creatorCampaignId, ReviewRound reviewRound,
                                                                  ContentType contentType);

    @Query("""
                select r.contentType
                from CampaignReview r
                where r.creatorCampaign.id = :creatorCampaignId
                  and r.reviewRound = :reviewRound
                order by r.id asc
            """)
    List<ContentType> findContentOnly(@Param("creatorCampaignId") Long creatorCampaignId,
                                      @Param("reviewRound") ReviewRound reviewRound);

    @Query("""
            select r from CampaignReview r
            join fetch r.creatorCampaign cc
            join fetch cc.creator cr
            where r.id = :reviewId
            """)
    Optional<CampaignReview> findWithCreatorCampaignById(@Param("reviewId") Long reviewId);

    Optional<CampaignReview> findTopByCreatorCampaignIdAndReviewRoundAndContentTypeOrderByIdAsc(
            Long creatorCampaignId, ReviewRound reviewRound, ContentType contentType);

    List<CampaignReview> findAllByCreatorCampaignIdOrderByIdAsc(Long creatorCampaignId);

    List<CampaignReview> findByCreatorCampaignAndReviewRoundOrderByIdDesc(
            CreatorCampaign creatorCampaign, ReviewRound reviewRound);

    Optional<CampaignReview> findTopByCreatorCampaignAndReviewRoundOrderByIdDesc(
            CreatorCampaign creatorCampaign, ReviewRound reviewRound);

    List<CampaignReview> findAllByCreatorCampaignId(Long creatorCampaignId);

    @Query("""
            select r.creatorCampaign.id, r.contentType
            from CampaignReview r
            where r.creatorCampaign.id in :creatorCampaignIds
              and r.reviewRound = :reviewRound
            """)
    List<Object[]> findContentTypesByCreatorCampaignIdsAndReviewRound(
            @Param("creatorCampaignIds") List<Long> creatorCampaignIds,
            @Param("reviewRound") ReviewRound reviewRound);
}
